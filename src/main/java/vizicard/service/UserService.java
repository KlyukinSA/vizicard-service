package vizicard.service;

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

import vizicard.dto.ContactDTO;
import vizicard.dto.UserResponseDTO;
import vizicard.dto.UserSignupDTO;
import vizicard.dto.UserUpdateDTO;
import vizicard.exception.CustomException;
import vizicard.model.AppUserRole;
import vizicard.model.Contact;
import vizicard.model.ContactType;
import vizicard.model.Profile;
import vizicard.repository.ContactRepository;
import vizicard.repository.ContactTypeRepository;
import vizicard.repository.ProfileRepository;
import vizicard.security.JwtTokenProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final ProfileRepository profileRepository;
  private final ContactRepository contactRepository;
  private final ContactTypeRepository contactTypeRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;
  private final ModelMapper modelMapper;

  public String signin(String username, String password) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      return jwtTokenProvider.createToken(username, Collections.singletonList(AppUserRole.ROLE_CLIENT));
    } catch (AuthenticationException e) {
      throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public String signup(UserSignupDTO dto) {
//    profile.setAppUserRoles(new ArrayList<>(Arrays.asList(AppUserRole.ROLE_CLIENT)));
    Profile profile = modelMapper.map(dto, Profile.class);
    if (!profileRepository.existsByUsername(profile.getUsername())) {
      profile.setPassword(passwordEncoder.encode(profile.getPassword()));
      profileRepository.save(profile);
      return jwtTokenProvider.createToken(profile.getUsername(), Collections.singletonList(AppUserRole.ROLE_CLIENT));
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
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Profile user = profileRepository.findByUsername(authentication.getName());

    return getUserResponseDTO(user);
  }

  public String refresh(String username) {
    return jwtTokenProvider.createToken(username, Collections.singletonList(AppUserRole.ROLE_CLIENT));
  }

  public UserResponseDTO update(UserUpdateDTO dto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Profile user = profileRepository.findByUsername(authentication.getName());

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

  private UserResponseDTO getUserResponseDTO(Profile user) {
    UserResponseDTO res = modelMapper.map(user, UserResponseDTO.class);
    res.setContacts(getUserContacts(user));
    return res;
  }

  private ContactDTO[] getUserContacts(Profile user) {
    Contact[] a = contactRepository.findByOwner(user);
    return Arrays.stream(a).map((val) -> new ContactDTO(val.getContactType().getContactEnum(), val.getContact())).toArray(ContactDTO[]::new);
  }

  private void updateContacts(Profile owner, ContactDTO[] list) {
    for (ContactDTO dto : list) {
      ContactType contactType = contactTypeRepository.findByContactEnum(dto.getContactEnum());
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
  }
}
