package vizicard.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import vizicard.repository.AccountRepository;
import vizicard.repository.CardRepository;
import vizicard.utils.LastVizitFilter;
import vizicard.utils.ProfileProvider;

@Configuration
@RequiredArgsConstructor
public class LastVizitFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ProfileProvider profileProvider;
    private final AccountRepository accountRepository;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(
                new LastVizitFilter(profileProvider, accountRepository), JwtTokenFilter.class);
    }

}
