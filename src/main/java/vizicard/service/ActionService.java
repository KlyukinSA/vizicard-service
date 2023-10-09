package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.dto.action.GraphActionResponse;
import vizicard.dto.action.PageActionDTO;
import vizicard.model.*;
import vizicard.repository.ActionRepository;
import vizicard.repository.ContactRepository;
import vizicard.utils.ProfileProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;
    private final ContactRepository contactRepository;

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

    public void addClickAction(Integer resourceId) {
        Contact resource = contactRepository.findById(resourceId).get();
        Profile target = resource.getOwner();
        Action click = new Action(profileProvider.getUserFromAuth(), target, ActionType.CLICK);
        click.setResource(resource);
        actionRepository.save(click);
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

	public List<GraphActionResponse> getDailyGraph(int days) {
        List<GraphActionResponse> res = Stream.generate(GraphActionResponse::new).limit(days).collect(Collectors.toList());
        Profile user = profileProvider.getUserFromAuth();
        Date stop = Date.from(Instant.now());
        Date start = Date.from(Instant.now().minus(Duration.ofDays(days)));
        int dayStart = start.getDay();

        List<Action> visits = actionRepository.findAllByProfileAndTypeAndCreateAtBetween(
                user, ActionType.VIZIT, start, stop);
        List<HashSet<Integer>> visitorSets = Stream.generate(() -> new HashSet<Integer>()).limit(days).collect(Collectors.toList());
        for (Action visit : visits) {
            Date createAt = visit.getCreateAt();
            int pos = createAt.getDay() - dayStart;

            GraphActionResponse response = res.get(pos);
            if (response.getDate() == null) {
                response.setDate(createAt);
            }

            response.setVizits(response.getVizits() + 1);

            if (!visitorSets.get(pos).contains(visit.getOwner().getId())) {
                visitorSets.get(pos).add(visit.getOwner().getId());

                response.setCoverage(response.getCoverage() + 1);
            }
            res.set(pos, response);
        }
        return res;
    }

    public Action addPartnership(Profile owner, Profile profile) {
        return actionRepository.save(new Action(owner, profile, ActionType.PARTNERSHIP));
    }

    public int countUniquePartnershipsByProfile(Profile user) {
        return actionRepository.countByProfileAndTypeDistinctByOwner(user, ActionType.PARTNERSHIP);
    }

}
