package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForInstant;
import org.springframework.stereotype.Service;
import vizicard.dto.PageActionDTO;
import vizicard.model.Action;
import vizicard.model.ActionType;
import vizicard.model.Profile;
import vizicard.repository.ActionRepository;
import vizicard.utils.ProfileProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;

    private final ProfileProvider profileProvider;

    public void vizit(Profile page) {
        Profile actor = profileProvider.getUserFromAuth();
        if (actor != null && Objects.equals(actor.getId(), page.getId())) {
            return;
        }
        actionRepository.save(new Action(actor, page, ActionType.VIZIT));
    }

    public void save(Profile owner, Profile target) {
        actionRepository.save(new Action(owner, target, ActionType.SAVE));
    }

    public void addClickAction(Integer targetProfileId) {
        Profile target = profileProvider.getTarget(targetProfileId);
        actionRepository.save(new Action(profileProvider.getUserFromAuth(), target, ActionType.CLICK));
    }

    public PageActionDTO getPageStats() {
        Profile user = profileProvider.getUserFromAuth();

        Date stop = Date.from(Instant.now());
        Date start = Date.from(Instant.now().minus(Duration.ofDays(7)));

        Function<ActionType, Integer> f = (actionType) ->
                actionRepository.countByPageAndCreateAtBetweenAndType(user, start, stop, actionType);

        return new PageActionDTO(f.apply(ActionType.VIZIT), f.apply(ActionType.SAVE), f.apply(ActionType.CLICK));
    }

}
