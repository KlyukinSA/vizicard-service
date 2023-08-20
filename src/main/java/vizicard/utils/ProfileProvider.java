package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vizicard.model.Profile;
import vizicard.repository.ProfileRepository;

@Component
@RequiredArgsConstructor
public class ProfileProvider {

    private final ProfileRepository profileRepository;

    public Profile getUserFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getName().equals("anonymousUser")) {
            return profileRepository.findById(Integer.valueOf(authentication.getName())).get();
        } else return null;
    }

}
