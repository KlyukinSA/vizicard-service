package vizicard.service;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.StreamWriter;
import ezvcard.io.text.VCardWriter;
import ezvcard.parameter.ImageType;
import ezvcard.property.Address;
import ezvcard.property.Photo;
import ezvcard.property.RawProperty;
import ezvcard.property.Url;
import io.netty.util.Signal;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.*;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.ContactRepository;
import vizicard.repository.ContactTypeRepository;
import vizicard.repository.DeviceRepository;
import vizicard.repository.ProfileRepository;
import vizicard.security.JwtTokenProvider;

import javax.sql.DataSource;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final ProfileRepository profileRepository;
  private final ContactRepository contactRepository;
  private final ContactTypeRepository contactTypeRepository;
  private final DeviceRepository deviceRepository;

  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;

  private final ModelMapper modelMapper;

  private final S3Service s3Service;

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
    Optional<Profile> profile = profileRepository.findById(id);
    if (!profile.isPresent()) {
      throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
    }
    return getUserResponseDTO(profile.get());
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

    updateContacts(user, dto.getContacts());

    profileRepository.save(user);

    return getUserResponseDTO(user);
  }

  private Profile getUserFromAuth() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return profileRepository.findById(Integer.valueOf(authentication.getName())).get();
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
      if (dto.getType() != ContactEnum.MAIL) {
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

  public byte[] getVcardBytes(Integer id) throws IOException {
    Profile profile = profileRepository.getById(id);
    VCard vcard = getVcard(profile);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    StreamWriter writer = new VCardWriter(outputStream, VCardVersion.V4_0);
    writer.write(vcard);
    writer.close();
    return outputStream.toByteArray();
  }

  private VCard getVcard(Profile user) {
    VCard vcard = new VCard();
    if (user.getName() != null) {
      vcard.setFormattedName(user.getName());
    }
    if (user.getPosition() != null) {
      vcard.addTitle(user.getPosition());
    }
    if (user.getDescription() != null) {
      vcard.addNote(user.getDescription());
    }
    if (user.getCompany() != null) {
      vcard.setOrganization(user.getCompany());
    }
    if (user.getCity() != null) {
      Address address = new Address();
      address.setLocality(user.getCity());
      vcard.addAddress(address);
    }

    int group = 0;
    ContactDTO[] contacts = getUserContacts(user);
    for (ContactDTO contact : contacts) {
      ContactEnum contactEnum = contact.getType();
      String string = contact.getContact();
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

    if (user.getAvatar() != null) {
      Photo photo = new Photo(user.getAvatar().getUrl(), ImageType.JPEG);
      vcard.addPhoto(photo); // TODO url or byte[]
    }

    return vcard;
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
}
