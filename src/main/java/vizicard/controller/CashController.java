package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vizicard.model.Profile;
import vizicard.repository.ProfileRepository;
import vizicard.utils.ProfileProvider;

@RestController
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {
    private final ProfileRepository profileRepository;
    private final ProfileProvider profileProvider;
    @PostMapping
    public void addCash(float amount) {
        Profile user = profileProvider.getUserFromAuth();
        user.setCash(user.getCash() + amount);
        profileRepository.save(user);
    }
}
