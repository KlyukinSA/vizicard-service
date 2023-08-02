package vizicard.security;

import lombok.RequiredArgsConstructor;
import vizicard.model.AppUserRole;
import vizicard.model.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vizicard.repository.ProfileRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MyUserDetails implements UserDetailsService {

  private final ProfileRepository profileRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final Profile profile = profileRepository.findByUsername(username);

    if (profile == null) {
      throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    return org.springframework.security.core.userdetails.User//
        .withUsername(username)//
        .password(profile.getPassword())//
        .authorities(Collections.singletonList(AppUserRole.ROLE_CLIENT))//
        .accountExpired(false)//
        .accountLocked(false)//
        .credentialsExpired(false)//
        .disabled(false)//
        .build();
  }

}
