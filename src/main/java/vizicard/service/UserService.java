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

import vizicard.exception.CustomException;
import vizicard.model.AppUserRole;
import vizicard.model.Profile;
import vizicard.repository.ProfileRepository;
import vizicard.security.JwtTokenProvider;

import java.util.ArrayList;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserService {

  private final ProfileRepository profileRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;

  public String signin(String username, String password) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      return jwtTokenProvider.createToken(username, profileRepository.findByUsername(username).getAppUserRoles());
    } catch (AuthenticationException e) {
      throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public String signup(Profile profile) {
    profile.setAppUserRoles(new ArrayList<>(Arrays.asList(AppUserRole.ROLE_CLIENT)));
    if (!profileRepository.existsByUsername(profile.getUsername())) {
      profile.setPassword(passwordEncoder.encode(profile.getPassword()));
      profileRepository.save(profile);
      return jwtTokenProvider.createToken(profile.getUsername(), profile.getAppUserRoles());
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
    return jwtTokenProvider.createToken(username, profileRepository.findByUsername(username).getAppUserRoles());
  }

  public Profile update(Profile mask) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Profile base = profileRepository.findByUsername(authentication.getName());

    mask.setId(base.getId());
    mask.setUsername(base.getUsername());
    mask.setPassword(base.getPassword());
    mask.setAppUserRoles(base.getAppUserRoles());

    return profileRepository.save(mask);
  }
}
