package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
    private final CashService cashService;

    public void addVisitAction(Card card, Shortname shortname) {
        Account visitor = profileProvider.getUserFromAuth();
        if (visitor != null && Objects.equals(visitor.getId(), card.getAccount().getId())) {
            return;
        }
        String ip = getIp();

        if (shortname != null) {
            Card referrer = shortname.getReferrer();
            if (referrer != null) {
//                actionRepository.findAllByOwnerAndProfileAndTypeAndIp()
                List<Action> visits = actionRepository.findAllByCardAndType(card, ActionType.VIZIT);
                if (isUniqueIn(visitor, ip, visits)) {
                    cashService.giveBonus(referrer.getAccount(), 1, card);
                    addGiveBonusAction(card, referrer.getAccount(), 1);
                }
            }
        }

        Action action = new Action(visitor, card, ActionType.VIZIT, ip);
        action.setShortname(shortname);
        actionRepository.save(action);
    }

    private boolean isUniqueIn(Account owner, String ip, List<Action> visits) {
        for (Action visit : visits) {
            if (owner == null) {
                if (visit.getAccountOwner() == null && visit.getIp().equals(ip)) {
                    return false;
                }
            } else if (visit.getAccountOwner() != null) {
                if (visit.getAccountOwner().getId().equals(owner.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addSaveAction(Account owner, Card target) {
        actionRepository.save(new Action(owner, target, ActionType.SAVE, getIp()));
    }

    public void addClickAction(Integer resourceId) {
        Contact resource = contactRepository.findById(resourceId).get();
        Card target = resource.getCardOwner();
        Action click = new Action(profileProvider.getUserFromAuth(), target, ActionType.CLICK, getIp());
        click.setResource(resource);
        actionRepository.save(click);
    }

    public PageActionDTO getPageStats() {
        Account user = profileProvider.getUserFromAuth();

        Date stop = Date.from(Instant.now());
        Date start = Date.from(Instant.now().minus(Duration.ofDays(7)));

        Function<ActionType, Integer> f = (actionType) ->
                actionRepository.countByCardAndCreateAtBetweenAndType(user.getCurrentCard(), start, stop, actionType);

        return new PageActionDTO(f.apply(ActionType.VIZIT), f.apply(ActionType.SAVE), f.apply(ActionType.CLICK));
    }

    public float getBenefitBetween(Instant minus, Instant now, Card card) {
        Date start = Date.from(minus);
        Date stop = Date.from(now);
        return countBenefit(actionRepository.findAllByCardAndTypeAndCreateAtBetween(
                card, ActionType.GIVE_BONUS, start, stop));
    }

    public float getBenefit(Card card) {
        return countBenefit(actionRepository.findAllByCardAndType(card, ActionType.GIVE_BONUS));
    }

    private float countBenefit(List<Action> actions) {
        return (float) actions.stream()
                .map(Action::getBonus)
                .mapToDouble(Float::doubleValue)
                .sum();
    }

	public List<GraphActionResponse> getDailyGraph(int days) {
        List<GraphActionResponse> res = Stream.generate(GraphActionResponse::new).limit(days).collect(Collectors.toList());
        Account user = profileProvider.getUserFromAuth();
        Date stop = Date.from(Instant.now());
        Date start = Date.from(Instant.now().minus(Duration.ofDays(days)));
        int dayStart = start.getDay();

        List<Action> visits = actionRepository.findAllByCardAndTypeAndCreateAtBetween(
                user.getCurrentCard(), ActionType.VIZIT, start, stop);
        List<HashSet<Integer>> visitorSets = Stream.generate(() -> new HashSet<Integer>()).limit(days).collect(Collectors.toList());
        for (Action visit : visits) {
            Date createAt = visit.getCreateAt();
            int pos = createAt.getDay() - dayStart;

            GraphActionResponse response = res.get(pos);
            if (response.getDate() == null) {
                response.setDate(createAt);
            }

            response.setVizits(response.getVizits() + 1);

            if (!visitorSets.get(pos).contains(visit.getAccountOwner().getId())) {
                visitorSets.get(pos).add(visit.getAccountOwner().getId());

                response.setCoverage(response.getCoverage() + 1);
            }
            res.set(pos, response);
        }
        return res;
    }

    private String getIp() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRemoteAddr();
    }

    public void addGiveBonusAction(Card card, Account referrer, float bonus) {
        Action action = new Action(card.getAccount(), referrer.getMainCard(), ActionType.GIVE_BONUS, getIp());
        action.setBonus(bonus);
        actionRepository.save(action);
    }

    public int countUniqueVisitsWhereShortnameReferrer(Card referrer) {
//        return actionRepository.countByShortnameReferrerAndTypeUniqueByOwnerAndIp(user, ActionType.VIZIT) select count(DISTINCT a.owner, a.ip) from Action as a join Shortname as s where s.referrer = ?1 and a.type = ?2
        int res = 0;
        List<Action> visits = actionRepository.findAllByShortnameReferrerAndType(referrer, ActionType.VIZIT);
        Set<Integer> ownerIds = new HashSet<>();
        Set<String> ips = new HashSet<>();
        for (Action visit : visits) {
            if (visit.getAccountOwner() != null) {
                if (!ownerIds.contains(visit.getAccountOwner().getId())) {
                    res++;
                    ownerIds.add(visit.getAccountOwner().getId());
                }
            } else {
                if (!ips.contains(visit.getIp())) {
                    res++;
                    ips.add(visit.getIp());
                }
            }
        }
        return res;
    }

    public List<Action> getProfilesIVisitedHistory() {
        return actionRepository.findAllByAccountOwnerAndTypeOrderByCreateAtDesc(profileProvider.getUserFromAuth(), ActionType.VIZIT);
    }

}
