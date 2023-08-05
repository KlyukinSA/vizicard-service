package vizicard.security;

import com.mysql.cj.x.protobuf.MysqlxCursor;
import lombok.RequiredArgsConstructor;
import vizicard.model.AppUserRole;
import vizicard.model.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vizicard.repository.ProfileRepository;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetails implements UserDetailsService {

  private final ProfileRepository profileRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//    final Profile profile = profileRepository.findByUsername(username);
    Optional<Profile> profile = profileRepository.findById(Integer.valueOf(username));

    if (!profile.isPresent()) {
      throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    return org.springframework.security.core.userdetails.User//
        .withUsername(username)//
        .password(profile.get().getPassword())//
        .authorities(Collections.singletonList(AppUserRole.ROLE_CLIENT))//
        .accountExpired(false)//
        .accountLocked(false)//
        .credentialsExpired(false)//
        .disabled(false)//
        .build();
  }

}
