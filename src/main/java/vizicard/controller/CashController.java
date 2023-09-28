package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vizicard.model.Profile;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.ProfileRepository;
import vizicard.repository.RelationRepository;
import vizicard.utils.ProfileProvider;

@RestController
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {
    private final ProfileRepository profileRepository;
    private final RelationRepository relationRepository;
    private final ProfileProvider profileProvider;
    @PostMapping
    public void addCash(float amount) {
        Profile user = profileProvider.getUserFromAuth();
        user.setCash(user.getCash() + amount);
        profileRepository.save(user);

        Relation created = relationRepository.findByTypeAndProfile(RelationType.CREATED_REFERRAL, user);
        if (created != null) {
            Profile creator = created.getOwner();
            creator.setCash((float) (creator.getCash() + 0.05 * amount));
            profileRepository.save(creator);
        }
    }
}
