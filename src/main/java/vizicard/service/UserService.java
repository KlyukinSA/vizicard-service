package vizicard.service;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vizicard.dto.ContactDTO;
import vizicard.dto.ContactRequest;
import vizicard.exception.CustomException;
import vizicard.model.AppUserRole;
import vizicard.model.Contact;
import vizicard.model.ContactType;
import vizicard.model.Profile;
import vizicard.repository.ContactRepository;
import vizicard.repository.ContactTypeRepository;
import vizicard.repository.ProfileRepository;
import vizicard.security.JwtTokenProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

  public Profile update(Profile mask) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Profile base = profileRepository.findByUsername(authentication.getName());

    base.setName(mask.getName());
    base.setPosition(mask.getPosition());
    base.setDescription(mask.getDescription());
    base.setCompany(mask.getCompany());
    base.setCity(mask.getCity());

    return profileRepository.save(base);
  }

  public void updateContacts(ContactRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Profile owner = profileRepository.findByUsername(authentication.getName());

    ContactDTO[] list = request.getContacts();
    for (ContactDTO dto : list) {
      ContactType contactType = contactTypeRepository.findById(dto.getContactTypeId()).get(); // TODO
      System.out.printf(contactType.toString() + "\n\n");
      Contact contact = contactRepository.findByOwnerAndContactType(owner, contactType);
      if (contact != null) {
        contact.setContact(dto.getContact());
        System.out.printf(contact.toString() + "\n\n");
      } else {
        System.out.printf("no contact" + "\n\n");
        contact = new Contact();
        contact.setContactType(contactType);
        contact.setOwner(owner);
        contact.setContact(dto.getContact());
      }
      contactRepository.save(contact);
    }
  }
}
