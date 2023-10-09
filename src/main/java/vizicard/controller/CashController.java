package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vizicard.model.*;
import vizicard.repository.ProfileRepository;
import vizicard.repository.RelationRepository;
import vizicard.service.ActionService;
import vizicard.service.CashService;
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
    private final ActionService actionService;
    private final CashService cashService;

    @PostMapping
    public void addCash(float amount) {
        Profile user = profileProvider.getUserFromAuth();

        Profile profile = primaryService.getPrimaryOrSelf(user);
        profile.setCash(profile.getCash() + amount);
        profileRepository.save(profile);

        addBonusToReferrer(RelationType.REFERRER, profile, amount);
        addBonusToReferrer(RelationType.REFERRER_LEVEL2, profile, amount);
    }

    private void addBonusToReferrer(RelationType referrerLevel, Profile profile, float amount) {
        Relation referrerRelation = relationRepository.findByTypeAndProfile(
                referrerLevel, profile);
        if (referrerRelation != null) {
            Profile referrer = referrerRelation.getOwner();

            float bonus = getBonusPart(referrerLevel) * amount;
            cashService.giveBonus(referrer, bonus);
            actionService.addGiveBonusAction(profile, referrer, bonus);
        }
    }

    private float getBonusPart(RelationType referrerLevel) {
        if (referrerLevel == RelationType.REFERRER_LEVEL2) {
            return 0.1F;
        } else if (referrerLevel == RelationType.REFERRER) {
            return 0.2F;
        } else {
            return 0;
        }
    }

}
