package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.dto.action.PageActionDTO;
import vizicard.model.*;
import vizicard.repository.ActionRepository;
import vizicard.repository.RelationRepository;
import vizicard.utils.ProfileProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;

    private final ProfileProvider profileProvider;
    private final RelationRepository relationRepository;

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
                actionRepository.countByProfileAndCreateAtBetweenAndType(user, start, stop, actionType);

        return new PageActionDTO(f.apply(ActionType.VIZIT), f.apply(ActionType.SAVE), f.apply(ActionType.CLICK));
    }

    public float getBenefitBetween(Instant minus, Instant now, Profile profile) {
        Date start = Date.from(minus);
        Date stop = Date.from(now);
        return countBenefit(actionRepository.findAllByProfileAndTypeAndCreateAtBetween(
						profile, ActionType.GIVE_BONUS, start, stop));
    }

    public float getBenefit(Profile profile) {
        return countBenefit(actionRepository.findAllByProfileAndType(profile, ActionType.GIVE_BONUS));
    }

    private float countBenefit(List<Action> actions) {
        return (float) actions.stream()
                .map(Action::getBonus)
                .mapToDouble(Float::doubleValue)
                .sum();
    }

    public List<Integer> getListOfCountOfActionsInDayByActionTypeAndBetween(ActionType actionType, Date from, Date to) {
        long fromTime = from.getTime();
        long diff = to.getTime() - fromTime;
        int days = (int) (diff / (24 * 60 * 60 * 1000));
        List<Integer> res = new ArrayList<>(Collections.nCopies(1 + days, 0));
        List<Action> actions = actionRepository.findAllByProfileAndTypeAndCreateAtBetween(profileProvider.getUserFromAuth(), actionType, from, to);
        for (Action action : actions) {
            Date createAt = action.getCreateAt();
            int pos = (int) (((float) days) * (createAt.getTime() - fromTime) / diff);
            res.set(pos, res.get(pos) + 1);
        }
        return res;
    }

//    public List<IActionCount> getProfileStats(Profile profile) {
//        return actionRepository.countActionStats(profile);
//    }

}
