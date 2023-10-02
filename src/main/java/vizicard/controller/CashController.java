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
import vizicard.service.PrimaryService;
import vizicard.utils.ProfileProvider;

@RestController
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {

    private final ProfileRepository profileRepository;
    private final RelationRepository relationRepository;
    private final ProfileProvider profileProvider;
    private final PrimaryService primaryService;

    @PostMapping
    public void addCash(float amount) {
        Profile user = profileProvider.getUserFromAuth();

        Profile profile = primaryService.getPrimaryOrSelf(user);
        profile.setCash(profile.getCash() + amount);
        profileRepository.save(profile);

        Relation referrerRelation = relationRepository.findByTypeAndProfile(
                RelationType.CREATED_REFERRAL, user);
        if (referrerRelation != null) {
            Profile referrer = referrerRelation.getOwner();

            profile = primaryService.getPrimaryOrSelf(referrer);
            profile.setReferralBonus((float) (profile.getReferralBonus() + 0.15 * amount));
            profileRepository.save(profile);
        }
    }
}
