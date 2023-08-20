package vizicard.service;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.text.VCardWriter;
import ezvcard.parameter.ImageType;
import ezvcard.property.Address;
import ezvcard.property.Photo;
import ezvcard.property.RawProperty;
import ezvcard.property.Url;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.*;
import vizicard.dto.detail.EducationDTO;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.model.detail.Education;
import vizicard.repository.*;
import vizicard.repository.detail.EducationRepository;
import vizicard.security.JwtTokenProvider;

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
  private final ContactRepository contactRepository;
  private final ContactTypeRepository contactTypeRepository;
  private final DeviceRepository deviceRepository;
  private final RelationRepository relationRepository;
  private final ActionRepository actionRepository;
  private final EducationRepository educationRepository;

  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;

  @Getter
  private final ModelMapper modelMapper;

  private final S3Service s3Service;
  private final EmailService emailService;

  public String signin(SigninDTO dto) {
    try {
      Profile profile = profileRepository.findByUsername(dto.getUsername());
      String id = String.valueOf(profile.getId());
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, dto.getPassword()));
      return jwtTokenProvider.createToken(id);
    } catch (Exception e) {
      throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public String signup(UserSignupDTO dto) {
    Profile profile = modelMapper.map(dto, Profile.class);
    if (!profileRepository.existsByUsername(profile.getUsername())) {
      profile.setPassword(passwordEncoder.encode(profile.getPassword()));
      profile.setProfileType(ProfileType.USER);
      profile = profileRepository.save(profile);
      updateContact(profile, new ContactRequest(ContactEnum.MAIL, profile.getUsername()));
      String id = String.valueOf(profile.getId());
      return jwtTokenProvider.createToken(id);
    } else {
      throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public ProfileResponseDTO search(Integer id) {
    Profile profile = getTarget(id);
    actionRepository.save(new Action(getUserFromAuth(), profile, ActionType.VIZIT));
    return getProfileResponseDTO(profile);
  }

  public ProfileResponseDTO whoami() {
    return getProfileResponseDTO(getUserFromAuth());
  }

  public ProfileResponseDTO updateMe(ProfileUpdateDTO dto) {
    return getProfileResponseDTO(updateProfile(getUserFromAuth(), dto));
  }

  public Profile getUserFromAuth() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.getName().equals("anonymousUser")) {
      return profileRepository.findById(Integer.valueOf(authentication.getName())).get();
    } else return null;
  }

  public ProfileResponseDTO getProfileResponseDTO(Profile profile) {
    ProfileResponseDTO res = modelMapper.map(profile, ProfileResponseDTO.class);
    res.setContacts(getContactDTOs(profile));
    if (profile.getCompany() == null || !profile.getCompany().isStatus()) {
      res.setCompany(null);
    }
    addDetails(res, profile);
    return res;
  }

  private void addDetails(ProfileResponseDTO res, Profile profile) {
    res.setEducations(profile.getEducation().stream()
            .filter(Education::isStatus)
            .map((val) -> modelMapper.map(val, EducationDTO.class))
            .collect(Collectors.toList()));
  }

  private ContactDTO[] getContactDTOs(Profile profile) {
    Contact[] a = contactRepository.findByOwner(profile);
    return Arrays.stream(a).map((val) -> new ContactDTO(
            val.getContactType().getContactEnum(),
            val.getContact(),
            val.getContactType().getLogo().getUrl())
    ).toArray(ContactDTO[]::new);
  }

  private void updateContacts(Profile owner, ContactRequest[] list) {
    for (ContactRequest dto : list) {
      if (dto.getType() != null && dto.getContact() != null) {
        updateContact(owner, dto);
      }
    }
  }

  private void updateContact(Profile owner, ContactRequest dto) {
    ContactType contactType = contactTypeRepository.findByContactEnum(dto.getType());
    Contact contact = contactRepository.findByOwnerAndContactType(owner, contactType);
    if (contact != null) {
      contact.setContact(dto.getContact());
    } else {
      contact = new Contact();
      contact.setContactType(contactType);
      contact.setOwner(owner);
      contact.setContact(dto.getContact());
    }
    contactRepository.save(contact);
  }

  public ProfileResponseDTO updateAvatar(MultipartFile file) throws IOException {
    Profile user = getUserFromAuth();
    user.setAvatar(s3Service.uploadFile(file));
    profileRepository.save(user);
    return getProfileResponseDTO(user);
  }

  public ProfileResponseDTO updateBackground(MultipartFile file) throws IOException {
    Profile user = getUserFromAuth();
    user.setBackground(s3Service.uploadFile(file));
    profileRepository.save(user);
    return getProfileResponseDTO(user);
  }

  public ResponseEntity<?> relate(Integer targetProfileId) throws Exception {
    Profile target = getTarget(targetProfileId);

    byte[] vCardBytes = getVcardBytes(getVcard(target)); // TODO class VcardFile
    String fileName = getVcardFileName(target); //

    Profile owner = getUserFromAuth();
    if (owner != null && !Objects.equals(target.getId(), owner.getId())) {
      try {
        emailService.sendRelation(getRelationEmail(owner), fileName, vCardBytes);
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
    ContactDTO[] contacts = getContactDTOs(profile);
    for (ContactDTO contact : contacts) {
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

  public boolean addDevice(String word) {
    Device device = deviceRepository.findByUrl(word);
    if (device == null) {
      device = new Device();
      device.setUrl(word);
      device.setOwner(getUserFromAuth());
      deviceRepository.save(device);
      return true;
    }
    return false;
  }

  public void unrelate(Integer targetProfileId) {
    Profile target = getTarget(targetProfileId);

    Profile owner = getUserFromAuth();
    Relation relation = relationRepository.findByOwnerAndProfile(owner, target);
    if (relation == null) {
      throw new CustomException("No such relation", HttpStatus.NOT_MODIFIED);
    }
    relationRepository.delete(relation);
  }

  public List<RelationResponseDTO> getRelations() {
    Profile owner = getUserFromAuth();
    return relationRepository.findAllByOwnerOrderByProfileNameAsc(owner)
            .stream().map((val) -> new RelationResponseDTO(
                    getProfileResponseDTO(val.getProfile()), val.getCreateAt()))
            .collect(Collectors.toList());
  }

  public void leadGenerate(Integer targetProfileId, LeadGenerationDTO dto) {
    Profile target = getTarget(targetProfileId);

    Profile author = getUserFromAuth();
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
    actionRepository.save(new Action(getUserFromAuth(), target, ActionType.CLICK));
  }

  public PageActionDTO getPageStats() {
    Profile user = getUserFromAuth();

    Date stop = Date.from(Instant.now());
    Date start = Date.from(Instant.now().minus(Duration.ofDays(7)));

    Function<ActionType, Integer> f = (actionType) ->
            actionRepository.countByPageAndCreateAtBetweenAndType(user, start, stop, actionType);

    return new PageActionDTO(f.apply(ActionType.VIZIT), f.apply(ActionType.SAVE), f.apply(ActionType.CLICK));
  }

  public ProfileResponseDTO updateMyCompany(ProfileUpdateDTO dto) {
    Profile user = getUserFromAuth();
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
    Profile user = getUserFromAuth();
    user.getCompany().setStatus(false);
    profileRepository.save(user.getCompany());
  }

  public ProfileResponseDTO updateMyCompanyAvatar(MultipartFile file) throws IOException {
    Profile company = getUserFromAuth().getCompany();
    company.setAvatar(s3Service.uploadFile(file));
    profileRepository.save(company);
    return getProfileResponseDTO(company);
  }

  public ProfileResponseDTO updateMyLastVizit() {
    Profile user = getUserFromAuth();
    user.setLastVizit(new Date());
    profileRepository.save(user);
    return getProfileResponseDTO(user);
  }

}
