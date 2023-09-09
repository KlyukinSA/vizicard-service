package vizicard.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import vizicard.repository.ProfileRepository;
import vizicard.utils.LastVizitFilter;
import vizicard.utils.ProfileProvider;

@Configuration
@RequiredArgsConstructor
public class LastVizitFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ProfileProvider profileProvider;
    private final ProfileRepository profileRepository;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(
                new LastVizitFilter(profileProvider, profileRepository), JwtTokenFilter.class);
    }

}
