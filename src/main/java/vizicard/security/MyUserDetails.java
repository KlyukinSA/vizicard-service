package vizicard.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vizicard.model.Account;
import vizicard.model.AppUserRole;
import vizicard.model.Card;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vizicard.repository.AccountRepository;
import vizicard.repository.CardRepository;
import vizicard.service.CashService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MyUserDetails implements UserDetailsService {

  private final AccountRepository accountRepository;
  private final CashService cashService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Account> account = accountRepository.findById(Integer.valueOf(username));

    if (!account.isPresent()) {
      throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    request.setAttribute("user", account.get());

    List<GrantedAuthority> authorities = new ArrayList<>(Arrays.asList(
            AppUserRole.ROLE_CLIENT));

    if (cashService.isPro(account.get())) {
      authorities.add((GrantedAuthority) () -> "PRO");
    }

    String password = account.get().getPassword();
    if (password == null) {
      password = "dummy";
    }

    return org.springframework.security.core.userdetails.User
        .withUsername(username)
        .password(password)
        .authorities(authorities)
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }

}
