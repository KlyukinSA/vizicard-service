package vizicard.service;

import javax.servlet.http.HttpServletRequest;

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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

  private final ProfileRepository profileRepository;
  private final ContactRepository contactRepository;
  private final ContactTypeRepository contactTypeRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;

  public String signin(String username, String password) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      return jwtTokenProvider.createToken(username, Collections.singletonList(AppUserRole.ROLE_CLIENT));
    } catch (AuthenticationException e) {
      throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public String signup(Profile profile) {
//    profile.setAppUserRoles(new ArrayList<>(Arrays.asList(AppUserRole.ROLE_CLIENT)));
    if (!profileRepository.existsByUsername(profile.getUsername())) {
      profile.setPassword(passwordEncoder.encode(profile.getPassword()));
      profileRepository.save(profile);
      return jwtTokenProvider.createToken(profile.getUsername(), Collections.singletonList(AppUserRole.ROLE_CLIENT));
    } else {
      throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public void delete(String username) {
    profileRepository.deleteByUsername(username);
  }

  public Profile search(String username) {
    Profile profile = profileRepository.findByUsername(username);
    if (profile == null) {
      throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
    }
    return profile;
  }

  public Profile whoami(HttpServletRequest req) {
    return profileRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
  }

  public String refresh(String username) {
    return jwtTokenProvider.createToken(username, Collections.singletonList(AppUserRole.ROLE_CLIENT));
  }

  public UserResponseDTO update(UserUpdateDTO dto, ModelMapper modelMapper) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Profile user = profileRepository.findByUsername(authentication.getName());

    user.setName(dto.getName());
    user.setPosition(dto.getPosition());
    user.setDescription(dto.getDescription());
    user.setCompany(dto.getCompany());
    user.setCity(dto.getCity());

    updateContacts(user, dto.getContacts());

    profileRepository.save(user);

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
        System.out.printf("no contact" + "\n");
        contact = new Contact();
        contact.setContactType(contactType);
        contact.setOwner(owner);
        contact.setContact(dto.getContact());
      }
      contactRepository.save(contact);
      System.out.printf(contact.toString() + "\n\n");
    }
  }
}
