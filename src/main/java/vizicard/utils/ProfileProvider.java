package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vizicard.exception.CustomException;
import vizicard.model.Profile;
import vizicard.repository.ProfileRepository;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class ProfileProvider {

    private final ProfileRepository profileRepository;

    public Profile getUserFromAuth() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Profile user = (Profile) request.getAttribute("user");
        return user;
    }

    public Profile getTarget(Integer id) {
        CustomException exception = new CustomException("The profile doesn't exist", HttpStatus.NOT_FOUND);
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> exception);
        if (!profile.isStatus()) {
            throw exception;
        }
        return profile;
    }

}
