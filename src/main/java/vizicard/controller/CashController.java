package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vizicard.model.*;
import vizicard.repository.AccountRepository;
import vizicard.repository.RelationRepository;
import vizicard.service.ActionService;
import vizicard.service.CashService;
import vizicard.service.PrimaryService;
import vizicard.utils.ProfileProvider;

@RestController
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {

    private final AccountRepository accountRepository;
    private final RelationRepository relationRepository;
    private final ProfileProvider profileProvider;
    private final CashService cashService;
    private final ActionService actionService;

    @PostMapping
    public void addCash(float amount) {
        Account user = profileProvider.getUserFromAuth();

        user.setCash(user.getCash() + amount);
        accountRepository.save(user);

        Card card = user.getMainCard(); // related as main card in signup()
        addBonusToReferrer(RelationType.REFERRER, card, amount);
        addBonusToReferrer(RelationType.REFERRER_LEVEL2, card, amount);
    }

    private void addBonusToReferrer(RelationType referrerLevel, Card card, float amount) {
        Relation referrerRelation = relationRepository.findByTypeAndCard(
                referrerLevel, card);
        if (referrerRelation != null) {
            Account referrer = referrerRelation.getOwner();

            float bonus = getBonusPart(referrerLevel) * amount;
            cashService.giveBonus(referrer, bonus, card);
            actionService.addGiveBonusAction(card, referrer, amount);
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
