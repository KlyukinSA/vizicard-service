package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vizicard.dto.*;
import vizicard.dto.contact.ContactInListRequest;
import vizicard.dto.profile.ProfileCreateDTO;
import vizicard.dto.profile.ProfileUpdateDTO;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.*;
import vizicard.utils.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final RelationRepository relationRepository; // TODO CreateProfileDTOMapper
  private final ShortnameRepository shortnameRepository;
  private final AlbumRepository albumRepository;
  private final ContactRepository contactRepository;
  private final ContactTypeRepository contactTypeRepository;

  private final ProfileProvider profileProvider;
  private final RelationValidator relationValidator;
  private final ProfileMapper profileMapper;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;
  private final Relator relator;

  private final S3Service s3Service; // TODO use CloudFileRepository
  private final ActionService actionService; // TODO ActionSaver?

  public ProfileResponseDTO searchByShortname(String shortname) {
    Profile profile = shortnameRepository.findByShortname(shortname).getOwner();
    return search(profile);
  }

  public ProfileResponseDTO searchById(Integer id) {
    Profile profile = profileProvider.getTarget(id);
    return search(profile);
  }

  private ProfileResponseDTO search(Profile profile) {
    if (profile.getType() == ProfileType.CUSTOM_USER || profile.getType() == ProfileType.CUSTOM_COMPANY || profile.getType() == ProfileType.GROUP) {
      relationValidator.stopNotOwnerOf(profile);
    }
    actionService.vizit(profile);
    return profileMapper.mapToResponse(profile);
  }

  public ProfileResponseDTO whoami() {
    return profileMapper.mapToResponse(profileProvider.getUserFromAuth());
  }

  public ProfileResponseDTO update(Integer id, ProfileUpdateDTO dto) {
    Profile target = profileProvider.getTarget(id);
    relationValidator.stopNotOwnerOf(target);
    return profileMapper.mapToResponse(updateProfile(target, dto));
  }

  public Profile createProfile(ProfileCreateDTO dto, Profile owner, String username, String password, RelationType relationType) {
    Profile profile = new Profile();
    profile.setType(dto.getType());
    profile.setName(dto.getName());
    profile.setUsername(username);
    profile = profileRepository.save(profile);

    if (owner != null) {
      relationRepository.save(new Relation(owner, profile, relationType));
    }
    shortnameRepository.save(new Shortname(profile, String.valueOf(UUID.randomUUID()), ShortnameType.MAIN));
    if (profile.getType() == ProfileType.USER || profile.getType() == ProfileType.COMPANY) {
      Album album = new Album(profile);
      albumRepository.save(album);
      profile.setAlbum(album);
    }

    ProfileUpdateDTO dto1 = modelMapper.map(dto, ProfileUpdateDTO.class);
    dto1.setPassword(password);
    return updateProfile(profile, dto1);
  }

  public Profile createMyProfile(ProfileCreateDTO dto, RelationType relationType) {
    Set<ProfileType> relationOrCompanyGroupProfileTypes = new HashSet<>(Arrays.asList(
            ProfileType.CUSTOM_USER, ProfileType.CUSTOM_COMPANY,
            ProfileType.COMPANY, ProfileType.GROUP));
    if (!relationOrCompanyGroupProfileTypes.contains(dto.getType())) {
      throw new CustomException("cant create with this type", HttpStatus.UNPROCESSABLE_ENTITY);
    }
    Profile owner = profileProvider.getUserFromAuth();
    return createProfile(dto, owner, null, null, relationType);
  }

  private Profile updateProfile(Profile profile, ProfileUpdateDTO dto) {
    if (dto.getName() != null) { // TODO set modelMapper how to map contacts and cloudFiles
      profile.setName(dto.getName());
    }
    if (dto.getTitle() != null) {
      profile.setTitle(dto.getTitle());
    }
    if (dto.getDescription() != null) {
      profile.setDescription(dto.getDescription());
    }
    if (dto.getCity() != null) {
      profile.setCity(dto.getCity());
    }

    if (dto.getAvatarId() != null) {
      if (dto.getAvatarId().equals(0)) {
        profile.setAvatar(null);
      } else {
        profile.setAvatar(s3Service.getById(dto.getAvatarId()));
      }
    }

    if (dto.getCompanyId() != null) {
      if (dto.getCompanyId().equals(0)) {
        profile.setCompany(null);
      } else {
        Profile company = profileProvider.getTarget(dto.getCompanyId());
        profile.setCompany(company);

        Relation relation = relationRepository.findByOwnerAndProfile(profile, company);
        RelationType relationType;
        if (relation != null) {
          relationType = relation.getType();
        } else {
          relationType = RelationType.USUAL;
        }
        relator.relate(profile, company, relationType);
      }
    }

    if (dto.getPassword() != null) {
      profile.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    if (dto.getContacts() != null) {
      updateContacts(profile, dto.getContacts());
    }

    profile = profileRepository.save(profile);

    return profile;
  }

  private void updateContacts(Profile profile, List<ContactInListRequest> contacts) {
    Set<ContactEnum> types = contacts.stream()
            .map(ContactInListRequest::getType)
            .collect(Collectors.toSet());
    if (types.size() != contacts.size()) {
      throw new CustomException("Cant update profile when types in list of contacts are not unique", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    for (Contact contact : profile.getContacts()) {
      contact.setStatus(false);
    }

//    int order = profile.getContacts().stream().mapToInt(Contact::getOrder).max().orElse(0);
    int order = 0;
    for (ContactInListRequest dto : contacts) {
      order++;
      ContactType contactType = contactTypeRepository.findByType(dto.getType());
      Contact contact = contactRepository.findByOwnerAndOrder(profile, order);
      if (contact == null) {
        contact = new Contact();
        contact.setOwner(profile);
        contact.setOrder(order);
      } else {
        contact.setStatus(true);
      }
      contact.setType(contactType);
      contact.setContact(dto.getContact());
      contactRepository.save(contact);
      profile.getContacts().add(contact);
    }
  }

  public void deleteProfile(Integer id) {
    Profile target = profileProvider.getTarget(id);
    relationValidator.stopNotOwnerOf(target);
    target.setStatus(false);
    profileRepository.save(target);
  }

  public Profile mergeCustomProfiles(Integer mainId, Integer secondaryId) {
    if (Objects.equals(mainId, secondaryId)) {
      throw new CustomException("Can merge only different profiles", HttpStatus.FORBIDDEN);
    }
    Profile main = profileProvider.getTarget(mainId);
    Profile secondary = profileProvider.getTarget(secondaryId);
    if (!checkCanMerge(main, secondary)) {
      throw new CustomException("cant merge profiles with this types", HttpStatus.FORBIDDEN);
    }

    applySecondaryContacts(main, secondary);
    setCustomType(main);
    profileRepository.save(main);

    secondary.setStatus(false);
    profileRepository.save(secondary);
    return main;
  }

  private void applySecondaryContacts(Profile main, Profile secondary) {
    List<Contact> mainContacts = main.getContacts();
    Set<ContactEnum> mainContactTypes = mainContacts.stream()
            .map((val) -> val.getType().getType())
            .collect(Collectors.toSet());
    int order = mainContacts.size();
    for (Contact contact : secondary.getContacts()) {
      if (!mainContactTypes.contains(contact.getType().getType())) {
        order++;
        contact.setOrder(order);
        contact.setOwner(main);
        mainContacts.add(contact);
      }
    }
    main.setContacts(mainContacts);
  }

  private void setCustomType(Profile main) {
    if (main.getType() == ProfileType.CUSTOM_USER || main.getType() == ProfileType.LEAD_USER) {
      main.setType(ProfileType.CUSTOM_USER);
    } else {
      main.setType(ProfileType.CUSTOM_COMPANY);
    }
  }

  private boolean checkCanMerge(Profile main, Profile secondary) {
    if (main.getType() == ProfileType.CUSTOM_USER || main.getType() == ProfileType.LEAD_USER) {
      return secondary.getType() == ProfileType.CUSTOM_USER || secondary.getType() == ProfileType.LEAD_USER;
    } else if (main.getType() == ProfileType.CUSTOM_COMPANY || main.getType() == ProfileType.LEAD_COMPANY) {
      return secondary.getType() == ProfileType.CUSTOM_COMPANY || secondary.getType() == ProfileType.LEAD_COMPANY;
    } else {
      return false;
    }
  }

  public List<Profile> getSecondaryPrimaryAccounts() {
    Profile user = profileProvider.getUserFromAuth();
    Profile primary = getPrimary(user);
    if (primary == null) {
      return getProfileWithHisSecondaryAccounts(user);
    } else {
      return getProfileWithHisSecondaryAccounts(primary);
    }
  }

  private List<Profile> getProfileWithHisSecondaryAccounts(Profile owner) {
    List<Profile> res = relationRepository.findAllByTypeAndOwner(
            RelationType.SECONDARY, owner).stream()
            .map(Relation::getProfile)
            .filter(Profile::isStatus)
            .collect(Collectors.toList());
    res.add(owner);
    return res;
  }

  public Profile getPrimary(Profile secondary) {
    Relation relation = relationRepository.findByTypeAndProfile(RelationType.SECONDARY, secondary);
    if (relation == null) {
      return null;
    }
    return relation.getOwner();
  }

}
