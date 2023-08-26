package vizicard.service;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.text.VCardWriter;
import ezvcard.parameter.ImageType;
import ezvcard.property.Address;
import ezvcard.property.Photo;
import ezvcard.property.RawProperty;
import ezvcard.property.Url;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import vizicard.utils.ContactUpdater;
import vizicard.utils.ProfileProvider;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final RelationRepository relationRepository;

  private final ContactUpdater contactUpdater;
  private final ModelMapper modelMapper;
  private final ProfileProvider profileProvider;

  private final S3Service s3Service;
  private final EmailService emailService;
  private final ActionService actionService;

  public ProfileResponseDTO search(Integer id) {
    Profile profile = profileProvider.getTarget(id);
    if (profile.getType() == ProfileType.CUSTOM) {
      stopNotOwnerOf(profile);
    }
    actionService.vizit(profile);
    return getProfileResponseDTO(profile);
  }

  public ProfileResponseDTO whoami() {
    return getProfileResponseDTO(profileProvider.getUserFromAuth());
  }

  public ProfileResponseDTO update(Integer id, ProfileUpdateDTO dto) {
    Profile target = profileProvider.getTarget(id);
    stopNotOwnerOf(target);
    return getProfileResponseDTO(updateProfile(target, dto));
  }

  private ProfileResponseDTO getProfileResponseDTO(Profile profile) {
    ProfileResponseDTO res = modelMapper.map(profile, ProfileResponseDTO.class); // TODO map except company and contacts and about
    if (profile.getCompany() == null || !profile.getCompany().isStatus()) { // TODO same checks
      res.setCompany(null);
    }
    res.setContacts(getContactDTOs(profile));
    res.setAbout(getAbout(profile));
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
                    .map((val) -> modelMapper.map(val, SkillResponseDTO.class))
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

  public ResponseEntity<?> relate(Integer targetProfileId) throws Exception {
    Profile target = profileProvider.getTarget(targetProfileId);

    byte[] vCardBytes = getVcardBytes(getVcard(target)); // TODO class VcardFile
    String fileName = getVcardFileName(target); //

    Profile owner = profileProvider.getUserFromAuth();
    if (owner != null && !Objects.equals(target.getId(), owner.getId())) {
      try {
        emailService.sendRelation(target.getUsername(), owner.getName(), owner.getId());
      } catch (Exception e) {
        System.out.println("tried to send message from " + owner.getId() + " to " + target.getId() + "\nbut\n");
        e.printStackTrace();
      }

      Relation relation = relationRepository.findByOwnerAndProfile(owner, target);
      if (relation == null) {
        relationRepository.save(new Relation(owner, target));
      }
    }

    actionService.save(owner, target);

    return getVcardResponse(vCardBytes, fileName);
  }

  private String getVcardFileName(Profile target) {
    return target.getName() + ".vcf";
  }

  private ResponseEntity<?> getVcardResponse(byte[] vcardBytes, String fileName) {
    return ResponseEntity.ok()
            .contentType(MediaType.valueOf("text/vcard"))
            .header("Content-Disposition", "attachment; filename=\"" + fileName + '\"')
            .contentLength(vcardBytes.length)
            .body(new InputStreamResource(new ByteArrayInputStream(vcardBytes)));
  }

  private byte[] getVcardBytes(VCard vcard) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    VCardWriter writer = new VCardWriter(outputStream, VCardVersion.V3_0);
    writer.getVObjectWriter().getFoldedLineWriter().setLineLength(null);
    writer.write(vcard);
    writer.close();

    return outputStream.toByteArray();
  }

  private VCard getVcard(Profile profile) throws IOException {
    VCard vcard = new VCard();
    if (isGoodForVcard(profile.getName())) {
      vcard.setFormattedName(profile.getName());
    }
    if (isGoodForVcard(profile.getTitle())) {
      vcard.addTitle(profile.getTitle());
    }
    if (isGoodForVcard(profile.getDescription())) {
      vcard.addNote(profile.getDescription());
    }
    if (profile.getCompany() != null && profile.getCompany().isStatus() &&
            isGoodForVcard(profile.getCompany().getName())) {
      vcard.setOrganization(profile.getCompany().getName());
    }
    if (isGoodForVcard(profile.getCity())) {
      Address address = new Address();
      address.setLocality(profile.getCity());
      vcard.addAddress(address);
    }

    int group = 0;
    for (ContactDTO contact : getContactDTOs(profile)) {
      ContactEnum contactEnum = contact.getType();
      String string = contact.getContact();
      if (isGoodForVcard(string)) {
        if (contactEnum == ContactEnum.PHONE) {
          vcard.addTelephoneNumber(string);
        } else if (contactEnum == ContactEnum.MAIL) {
          vcard.addEmail(string);
        } else if (contactEnum == ContactEnum.SITE) {
          vcard.addUrl(string);
        } else {
          group++;
          String groupName = "item" + group;
          String type = contactEnum.toString();
          RawProperty property = vcard.addExtendedProperty("X-ABLABEL", type);
          property.setGroup(groupName);
          Url url = vcard.addUrl(string);
          url.setGroup(groupName);
        }
      }
    }

    if (profile.getAvatar() != null) {
      String url = profile.getAvatar().getUrl();
      InputStream inputStream = new BufferedInputStream(new URL(url).openStream());
      Photo photo = new Photo(inputStream, ImageType.JPEG);
      vcard.addPhoto(photo); // TODO image types
    }

    return vcard;
  }

  private boolean isGoodForVcard(String string) {
    return string != null && string.length() > 0;
  }

  public void unrelate(Integer targetProfileId) {
    Profile target = profileProvider.getTarget(targetProfileId);

    Profile owner = profileProvider.getUserFromAuth();
    Relation relation = relationRepository.findByOwnerAndProfile(owner, target);
    if (relation == null) {
      throw new CustomException("No such relation", HttpStatus.NOT_MODIFIED);
    }
    relationRepository.delete(relation);
  }

  public List<RelationResponseDTO> getRelations() {
    Profile owner = profileProvider.getUserFromAuth();
    return relationRepository.findAllByOwnerOrderByProfileNameAsc(owner)
            .stream().map((val) -> modelMapper.map(val, RelationResponseDTO.class))
            .collect(Collectors.toList());
  }

  public void leadGenerate(Integer targetProfileId, LeadGenerationDTO dto) {
    Profile target = profileProvider.getTarget(targetProfileId);

    Profile author = profileProvider.getUserFromAuth();
    if (author != null) {
      if (Objects.equals(target.getId(), author.getId())) return;
      Relation relation = relationRepository.findByOwnerAndProfile(target, author);
      if (relation == null) {
        relationRepository.save(new Relation(target, author));
      }
    }

    try {
      emailService.sendUsual(target.getUsername(), "Вам прислали новый контакт в ViziCard", getLeadGenMessage(dto, author));
    } catch (Exception ignored) {}
  }

  private String getLeadGenMessage(LeadGenerationDTO dto, Profile author) {
    String res = dto.toString();
    if (author != null) {
      res += "\n\n" + getProfileResponseDTO(author);
    }
    return res;
  }

  public ProfileResponseDTO createProfile(ProfileCreateDTO dto) {
    Profile owner = profileProvider.getUserFromAuth();
    Profile profile = new Profile();
    profile.setOwnerId(owner.getId());
    profile.setType(dto.getType());
    profile.setName(dto.getName());
    profile = profileRepository.save(profile);
    relationRepository.save(new Relation(owner, profile));
    return getProfileResponseDTO(updateProfile(profile, dto));
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
        profile.setCompany(profileProvider.getTarget(dto.getCompanyId()));
      }
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
    stopNotOwnerOf(target);
    target.setStatus(false);
    profileRepository.save(target);
  }

  private void stopNotOwnerOf(Profile target) {
    if (profileProvider.getUserFromAuth() == null ||
            !Objects.equals(target.getOwnerId(), profileProvider.getUserFromAuth().getId())) {
      throw new CustomException("You are not the owner of this profile", HttpStatus.FORBIDDEN);
    }
  }

  public void addGroupMembers(Integer groupId, List<Integer> memberIds) {
    Set<ProfileType> goodTypes = new HashSet<>(Arrays.asList(
            ProfileType.USER, ProfileType.CUSTOM, ProfileType.COMPANY));

    Profile group = profileProvider.getTarget(groupId);
    Profile user = profileProvider.getUserFromAuth();
    if (!Objects.equals(group.getOwnerId(), user.getId())) {
      throw new CustomException("You are not owner of this group", HttpStatus.FORBIDDEN);
    }

    for (Integer memberId : memberIds) {
      Profile profile = profileProvider.getTarget(memberId);
      if (goodTypes.contains(profile.getType())) {
        relationRepository.save(new Relation(profile, group));
      }
    }
  }
}
