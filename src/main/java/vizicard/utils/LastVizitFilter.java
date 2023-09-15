package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;
import vizicard.model.Profile;
import vizicard.repository.ProfileRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class LastVizitFilter extends GenericFilterBean {

    private final ProfileProvider profileProvider;
    private final ProfileRepository profileRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Profile user = profileProvider.getUserFromAuth();
        if (user != null) {
            user.setLastVizit(new Date());
            profileRepository.save(user);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
