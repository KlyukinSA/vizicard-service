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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.*;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.*;
import vizicard.security.JwtTokenProvider;

import java.io.*;
import java.net.URL;
import java.security.Principal;
import java.util.*;
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

  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;

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
      profile = profileRepository.save(profile);
      updateContact(profile, new ContactRequest(ContactEnum.MAIL, profile.getUsername()));
      String id = String.valueOf(profile.getId());
      return jwtTokenProvider.createToken(id);
    } else {
      throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public UserResponseDTO search(Integer id) {
    Profile profile = profileRepository.findById(id)
            .orElseThrow(() -> new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND));
    actionRepository.save(new Action(null, getUserFromAuth(), profile, null, ActionType.VIZIT));
    return getUserResponseDTO(profile);
  }

  public UserResponseDTO whoami() {
    return getUserResponseDTO(getUserFromAuth());
  }

  public UserResponseDTO update(UserUpdateDTO dto) {
    Profile user = getUserFromAuth();

    if (dto.getName() != null) {
      user.setName(dto.getName());
    }
    if (dto.getPosition() != null) {
      user.setPosition(dto.getPosition());
    }
    if (dto.getDescription() != null) {
      user.setDescription(dto.getDescription());
    }
    if (dto.getCompany() != null) {
      user.setCompany(dto.getCompany());
    }
    if (dto.getCity() != null) {
      user.setCity(dto.getCity());
    }

    if (dto.getContacts() != null) {
      updateContacts(user, dto.getContacts());
    }

    profileRepository.save(user);

    return getUserResponseDTO(user);
  }

  private Profile getUserFromAuth() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.getName().equals("anonymousUser")) {
      return profileRepository.findById(Integer.valueOf(authentication.getName())).get();
    } else return null;
  }

  private UserResponseDTO getUserResponseDTO(Profile user) {
    UserResponseDTO res = modelMapper.map(user, UserResponseDTO.class);
    res.setContacts(getUserContacts(user));
    return res;
  }

  private ContactDTO[] getUserContacts(Profile user) {
    Contact[] a = contactRepository.findByOwner(user);
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

  public UserResponseDTO updateAvatar(MultipartFile file) throws IOException {
    Profile user = getUserFromAuth();
    user.setAvatar(s3Service.uploadFile(file));
    profileRepository.save(user);
    return getUserResponseDTO(user);
  }

  public UserResponseDTO updateBackground(MultipartFile file) throws IOException {
    Profile user = getUserFromAuth();
    user.setBackground(s3Service.uploadFile(file));
    profileRepository.save(user);
    return getUserResponseDTO(user);
  }

  public ResponseEntity<?> relate(Integer targetProfileId) throws Exception {
    Profile target = profileRepository.findById(targetProfileId)
            .orElseThrow(() -> new CustomException("The target user doesn't exist", HttpStatus.NOT_FOUND));

    byte[] vCardBytes = getVcardBytes(getVcard(target)); // TODO class VcardFile
    String fileName = getVcardFileName(target); //

    Profile owner = getUserFromAuth();
    if (owner != null && !Objects.equals(target.getId(), owner.getId())) {
      emailService.sendRelation(getRelationEmail(owner), fileName, vCardBytes);

      Relation relation = relationRepository.findByOwnerAndProfile(owner, target);
      if (relation == null) {
        relationRepository.save(new Relation(owner, target));
      }
    }

    actionRepository.save(new Action(null, owner, target, null, ActionType.SAVE));

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

  private VCard getVcard(Profile user) throws IOException {
    VCard vcard = new VCard();
    if (isGoodForVcard(user.getName())) {
      vcard.setFormattedName(user.getName());
    }
    if (isGoodForVcard(user.getPosition())) {
      vcard.addTitle(user.getPosition());
    }
    if (isGoodForVcard(user.getDescription())) {
      vcard.addNote(user.getDescription());
    }
    if (isGoodForVcard(user.getCompany())) {
      vcard.setOrganization(user.getCompany());
    }
    if (isGoodForVcard(user.getCity())) {
      Address address = new Address();
      address.setLocality(user.getCity());
      vcard.addAddress(address);
    }

    int group = 0;
    ContactDTO[] contacts = getUserContacts(user);
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

    if (user.getAvatar() != null) {
      String url = user.getAvatar().getUrl();
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
    Profile target = profileRepository.findById(targetProfileId)
            .orElseThrow(() -> new CustomException("The target user doesn't exist", HttpStatus.NOT_FOUND));
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
                    getUserResponseDTO(val.getProfile()), val.getCreateAt()))
            .collect(Collectors.toList());
  }

  public void leadGenerate(Integer targetProfileId, LeadGenerationDTO dto) {
    Profile target = profileRepository.findById(targetProfileId)
            .orElseThrow(() -> new CustomException("The target user doesn't exist", HttpStatus.NOT_FOUND));

    Profile author = getUserFromAuth();
    if (author != null) {
      if (Objects.equals(target.getId(), author.getId())) return;

//      if (dto.getName() == null) {
//        dto.setName(author.getName());
//      }
//      if (dto.getPosition() == null) {
//        dto.setPosition(author.getPosition());
//      }

      Relation relation = relationRepository.findByOwnerAndProfile(target, author);
      if (relation == null) {
        relationRepository.save(new Relation(target, author));
      }
    }

    emailService.sendUsual(target.getUsername(), "Вам прислали новый контакт в ViziCard", getLeadGenMessage(dto, author));
  }

  private String getLeadGenMessage(LeadGenerationDTO dto, Profile author) {
    String res = dto.toString();
    if (author != null) {
      res += "\n\n" + getUserResponseDTO(author);
    }
    return res;
  }

  public void addClickAction(Integer targetProfileId) {
    Profile target = profileRepository.findById(targetProfileId)
            .orElseThrow(() -> new CustomException("The target user doesn't exist", HttpStatus.NOT_FOUND));
    actionRepository.save(new Action(null, getUserFromAuth(), target, null, ActionType.CLICK));
  }

}
