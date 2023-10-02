package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vizicard.model.Profile;
import vizicard.model.ProfileType;
import vizicard.model.Relation;
import vizicard.repository.ProfileRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CashDecreaseEngine {

    private final ProfileRepository profileRepository;
    private final PrimaryService primaryService;

    @Scheduled(fixedRate = 1000*60*60)
    private void decreaseGlobalCash() {
        profileRepository.findAll().stream()
                .filter(profile -> null == primaryService.getPrimary(profile))
                .forEach(this::decrease);
    }

    private void decrease(Profile profile) {
        float cash = profile.getCash();
        if (cash > 0) {
            cash -= getDecreasePrice(profile);
            if (cash < 0) {
                cash = 0;
            }
            profile.setCash(cash);
            profileRepository.save(profile);
        }
    }

    private float getDecreasePrice(Profile profile) {
        float res = 1;
        Profile company = profile.getCompany();
        if (company != null && company.isStatus()) {
            res += (float) (0.5 * company.getRelationsWhereProfile().stream()
                    .filter(Relation::isStatus)
                    .map(Relation::getOwner)
                    .filter(Profile::isStatus)
                    .filter(owner -> owner.getType() == ProfileType.WORKER)
                    .count());
        }
        return res;
    }

}
