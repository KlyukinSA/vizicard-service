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

import org.springframework.web.multipart.MultipartFile;
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
public class UserService {

  private final ProfileRepository profileRepository;
  private final RelationRepository relationRepository;
  private final ActionRepository actionRepository;
  private final ContactUpdater contactUpdater;

  private final ModelMapper modelMapper;
  private final ProfileProvider auther;

  private final S3Service s3Service;
  private final EmailService emailService;

  public ProfileResponseDTO search(Integer id) {
    Profile profile = getTarget(id);
    actionRepository.save(new Action(auther.getUserFromAuth(), profile, ActionType.VIZIT));
    return getProfileResponseDTO(profile);
  }

  public ProfileResponseDTO whoami() {
    return getProfileResponseDTO(auther.getUserFromAuth());
  }

  public ProfileResponseDTO updateMe(ProfileUpdateDTO dto) {
    return getProfileResponseDTO(updateProfile(auther.getUserFromAuth(), dto));
  }

  private ProfileResponseDTO getProfileResponseDTO(Profile profile) {
    ProfileResponseDTO res = modelMapper.map(profile, ProfileResponseDTO.class); // TODO map except education and contacts
    res.setContacts(getContactDTOs(profile));
    if (profile.getCompany() == null || !profile.getCompany().isStatus()) {
      res.setCompany(null);
    }
    res.setAbout(getAbout(profile));
    return res;
  }

  private ProfileDetailStructResponseDTO getAbout(Profile profile) {
      ProfileDetailStruct detailStruct = profile.getDetailStruct();
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
                    val.getContactType().getContactEnum(),
                    val.getContact(),
                    val.getContactType().getLogo().getUrl())
    ).collect(Collectors.toList());
  }

  private void updateContacts(Profile owner, ContactRequest[] list) {
    for (ContactRequest dto : list) {
      if (dto.getType() != null && dto.getContact() != null) {
        contactUpdater.updateContact(owner, dto);
      }
    }
  }

  public ProfileResponseDTO updateAvatar(MultipartFile file) throws IOException {
    Profile user = auther.getUserFromAuth();
    user.setAvatar(s3Service.uploadFile(file));
    profileRepository.save(user);
    return getProfileResponseDTO(user);
  }

  public ProfileResponseDTO updateBackground(MultipartFile file) throws IOException {
    Profile user = auther.getUserFromAuth();
    user.setBackground(s3Service.uploadFile(file));
    profileRepository.save(user);
    return getProfileResponseDTO(user);
  }

  public ResponseEntity<?> relate(Integer targetProfileId) throws Exception {
    Profile target = getTarget(targetProfileId);

    byte[] vCardBytes = getVcardBytes(getVcard(target)); // TODO class VcardFile
    String fileName = getVcardFileName(target); //

    Profile owner = auther.getUserFromAuth();
    if (owner != null && !Objects.equals(target.getId(), owner.getId())) {
      try {
        emailService.sendRelation(getRelationEmail(owner), fileName, vCardBytes, owner.getName(), owner.getId());
      } catch (Exception ignored) {}

      Relation relation = relationRepository.findByOwnerAndProfile(owner, target);
      if (relation == null) {
        relationRepository.save(new Relation(owner, target));
      }
    }

    actionRepository.save(new Action(owner, target, ActionType.SAVE));

    return getVcardResponse(vCardBytes, fileName);
  }

  private String getVcardFileName(Profile target) {
    return target.getName() + ".vcf";
  }

  private String getRelationEmail(Profile profile) {
    return profile.getUsername();
  }

  private ResponseEntity<?> getVcardResponse(byte[] vcardBytes, String fileName) {
    return ResponseEntity.ok()
            .contentType(MediaType.valueOf("text/vcard"))
            .header("Content-Disposition", "attachment; filename=\"" + fileName + '\"')
            .contentLength(vcardBytes.length)
            .body(new InputStreamResource(new ByteArrayInputStream(vcardBytes)));
  }

  public byte[] getVcardBytes(VCard vcard) throws IOException {
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
    if (profile.getCompany() != null && isGoodForVcard(profile.getCompany().getName())) {
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
    Profile target = getTarget(targetProfileId);

    Profile owner = auther.getUserFromAuth();
    Relation relation = relationRepository.findByOwnerAndProfile(owner, target);
    if (relation == null) {
      throw new CustomException("No such relation", HttpStatus.NOT_MODIFIED);
    }
    relationRepository.delete(relation);
  }

  public List<RelationResponseDTO> getRelations() {
    Profile owner = auther.getUserFromAuth();
    return relationRepository.findAllByOwnerOrderByProfileNameAsc(owner)
            .stream().map((val) -> modelMapper.map(val, RelationResponseDTO.class))
            .collect(Collectors.toList());
  }

  public void leadGenerate(Integer targetProfileId, LeadGenerationDTO dto) {
    Profile target = getTarget(targetProfileId);

    Profile author = auther.getUserFromAuth();
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

  public void addClickAction(Integer targetProfileId) {
    Profile target = getTarget(targetProfileId);
    actionRepository.save(new Action(auther.getUserFromAuth(), target, ActionType.CLICK));
  }

  public PageActionDTO getPageStats() {
    Profile user = auther.getUserFromAuth();

    Date stop = Date.from(Instant.now());
    Date start = Date.from(Instant.now().minus(Duration.ofDays(7)));

    Function<ActionType, Integer> f = (actionType) ->
            actionRepository.countByPageAndCreateAtBetweenAndType(user, start, stop, actionType);

    return new PageActionDTO(f.apply(ActionType.VIZIT), f.apply(ActionType.SAVE), f.apply(ActionType.CLICK));
  }

  public ProfileResponseDTO updateMyCompany(ProfileUpdateDTO dto) {
    Profile user = auther.getUserFromAuth();
    Profile company = user.getCompany();
    if (company == null || !company.isStatus()) {
      company = new Profile();
      company.setName(dto.getName());
      company.setProfileType(ProfileType.COMPANY);
      profileRepository.save(company);
      user.setCompany(company);
      profileRepository.save(user);
    }
    return getProfileResponseDTO(updateProfile(company, dto));
  }

  private Profile updateProfile(Profile profile, ProfileUpdateDTO dto) {
    if (dto.getName() != null) {
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

    if (dto.getContacts() != null) {
      updateContacts(profile, dto.getContacts());
    }

    return profileRepository.save(profile);
  }

  Profile getTarget(Integer id) {
    CustomException exception = new CustomException("The profile doesn't exist", HttpStatus.NOT_FOUND);
    Profile profile = profileRepository.findById(id)
            .orElseThrow(() -> exception);
    if (!profile.isStatus()) {
      throw exception;
    }
    return profile;
  }

  public void deleteMyCompany() {
    Profile user = auther.getUserFromAuth();
    user.getCompany().setStatus(false);
    profileRepository.save(user.getCompany());
  }

  public ProfileResponseDTO updateMyCompanyAvatar(MultipartFile file) throws IOException {
    Profile company = auther.getUserFromAuth().getCompany();
    company.setAvatar(s3Service.uploadFile(file));
    profileRepository.save(company);
    return getProfileResponseDTO(company);
  }

  public ProfileResponseDTO updateMyLastVizit() {
    Profile user = auther.getUserFromAuth();
    user.setLastVizit(new Date());
    profileRepository.save(user);
    return getProfileResponseDTO(user);
  }

}
