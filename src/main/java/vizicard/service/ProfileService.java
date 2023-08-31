package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vizicard.dto.*;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;
import vizicard.model.detail.ProfileDetailStruct;
import vizicard.model.detail.Skill;
import vizicard.repository.*;
import vizicard.utils.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final RelationRepository relationRepository; // TODO GroupService, RelationSaver
  private final ShortnameRepository shortnameRepository;

  private final ContactUpdater contactUpdater;
  private final ModelMapper modelMapper;
  private final ProfileProvider profileProvider;
  private final RelationValidator relationValidator;
  private final ProfileMapper profileMapper;
  private final PasswordEncoder passwordEncoder;
  private final ProfileCompanySetter profileCompanySetter;

  private final S3Service s3Service; // TODO CloudFileProvider
  private final ActionService actionService; // TODO ActionSaver

  public ProfileResponseDTO searchByShortname(String shortname) {
    Profile profile = shortnameRepository.findByShortname(shortname).getOwner();
    return search(profile);
  }

  public ProfileResponseDTO searchById(Integer id) {
    Profile profile = profileProvider.getTarget(id);
    return search(profile);
  }

  private ProfileResponseDTO search(Profile profile) {
    if (profile.getType() == ProfileType.CUSTOM || profile.getType() == ProfileType.GROUP) {
      relationValidator.stopNotOwnerOf(profile);
    }
    actionService.vizit(profile);
    return getProfileResponseDTO(profile);
  }

  public ProfileResponseDTO whoami() {
    return getProfileResponseDTO(profileProvider.getUserFromAuth());
  }

  public ProfileResponseDTO update(Integer id, ProfileUpdateDTO dto) {
    Profile target = profileProvider.getTarget(id);
    relationValidator.stopNotOwnerOf(target);
    return getProfileResponseDTO(updateProfile(target, dto));
  }

  private ProfileResponseDTO getProfileResponseDTO(Profile profile) { // TODO move to ProfileMapper
    ProfileResponseDTO res = modelMapper.map(profile, ProfileResponseDTO.class); // TODO map except company and contacts and about
    if (profile.getCompany() == null || !profile.getCompany().isStatus()) { // TODO same checks
      res.setCompany(null);
    } else {
      res.getCompany().setMainShortname(profileMapper.getMainShortname(profile.getCompany()));
    }
    res.setContacts(getContactDTOs(profile));
    res.setAbout(getAbout(profile));
    res.setMainShortname(profileMapper.getMainShortname(profile));
    return res;
  }

  private ProfileDetailStructResponseDTO getAbout(Profile profile) {
      ProfileDetailStruct detailStruct = profile.getDetailStruct();
      if (detailStruct == null) {
        return null;
      }
      return new ProfileDetailStructResponseDTO(
            detailStruct.getEducation().stream()
                    .filter(Education::isStatus)
                    .map((val) -> modelMapper.map(val, EducationResponseDTO.class))
                    .collect(Collectors.toList()),
            detailStruct.getExperience().stream()
                    .filter(Experience::isStatus)
                    .map((val) -> modelMapper.map(val, ExperienceResponseDTO.class))
                    .collect(Collectors.toList()),
            detailStruct.getSkills().stream()
                    .filter(Skill::isStatus)
                    .map(Skill::getSkill)
                    .collect(Collectors.toList())
            );
  }

  private List<ContactDTO> getContactDTOs(Profile profile) {
    if (profile.getContacts() == null) {
      return new ArrayList<>();
    }
    return profile.getContacts().stream()
            .map((val) -> new ContactDTO(
                    val.getType().getType(),
                    val.getContact(),
                    val.getType().getLogo().getUrl())
    ).collect(Collectors.toList());
  }

  private void updateContacts(Profile owner, ContactRequest[] list) {
    for (ContactRequest dto : list) {
      if (dto.getType() != null && dto.getContact() != null) {
        contactUpdater.updateContact(owner, dto);
      }
    }
  }

  public Profile createProfile(ProfileCreateDTO dto, Profile owner, String username) {
    Profile profile = new Profile();
    profile.setType(dto.getType());
    profile.setName(dto.getName());
    profile.setUsername(username);
    profile = profileRepository.save(profile);

    if (owner == null) {
      owner = profile;
    }
    relationRepository.save(new Relation(owner, profile, RelationType.OWNER));
    shortnameRepository.save(new Shortname(profile, String.valueOf(UUID.randomUUID()), ShortnameType.MAIN));

    return updateProfile(profile, dto);
  }

  public ProfileResponseDTO createMyProfile(ProfileCreateDTO dto) {
    Profile owner = profileProvider.getUserFromAuth();
    createProfile(dto, owner, null);
    return null;
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
        profileCompanySetter.setCompany(profile, profileProvider.getTarget(dto.getCompanyId()));
      }
    }

    if (dto.getPassword() != null) {
      profile.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    profile = profileRepository.save(profile);

    if (dto.getContacts() != null) {
      updateContacts(profile, dto.getContacts());
    }

    return profile;
  }

  public ProfileResponseDTO updateMyLastVizit() {
    Profile user = profileProvider.getUserFromAuth();
    user.setLastVizit(new Date());
    profileRepository.save(user);
    return getProfileResponseDTO(user);
  }

  public void deleteProfile(Integer id) {
    Profile target = profileProvider.getTarget(id);
    relationValidator.stopNotOwnerOf(target);
    target.setStatus(false);
    profileRepository.save(target);
  }

  public void addGroupMembers(Integer groupId, List<Integer> memberIds) {
    Set<ProfileType> goodTypes = new HashSet<>(Arrays.asList(
            ProfileType.USER, ProfileType.CUSTOM, ProfileType.COMPANY));

    Profile group = profileProvider.getTarget(groupId);
    letGroupPass(group);
    relationValidator.stopNotOwnerOf(group);

    for (Integer memberId : memberIds) {
      Profile profile = profileProvider.getTarget(memberId);
      if (goodTypes.contains(profile.getType())) {
        relationRepository.save(new Relation(profile, group, RelationType.USUAL));
      }
    }
  }

  public List<BriefResponseDTO> getAllMyGroups() {
    Profile user = profileProvider.getUserFromAuth();
    return relationRepository.findAllByOwnerAndProfileType(user, ProfileType.GROUP).stream()
            .map(Relation::getProfile)
            .filter(Profile::isStatus)
            .map(profileMapper::mapBrief)
            .collect(Collectors.toList());
  }

  public List<BriefResponseDTO> getAllGroupMembers(Integer groupId) {
    Profile group = profileProvider.getTarget(groupId);
    letGroupPass(group);
    Integer ownerId = relationRepository.findByTypeAndProfile(RelationType.OWNER, group).getOwner().getId();

    return relationRepository.findAllByProfile(group).stream()
            .filter(Relation::isStatus)
            .map(Relation::getOwner)
            .filter(Profile::isStatus)
            .filter((val) -> !Objects.equals(val.getId(), ownerId))
            .map(profileMapper::mapBrief)
            .collect(Collectors.toList());
  }

  void letGroupPass(Profile group) {
    if (group.getType() != ProfileType.GROUP) {
      throw new CustomException("This profile should be a group", HttpStatus.FORBIDDEN);
    }
  }

}
