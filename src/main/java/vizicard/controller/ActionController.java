package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.BriefCardResponse;
import vizicard.dto.action.GraphActionResponse;
import vizicard.dto.action.PageActionDTO;
import vizicard.dto.action.ReferralStatsDTO;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;
import vizicard.service.ActionService;
import vizicard.mapper.CardMapper;
import vizicard.utils.ProfileProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    private final RelationRepository relationRepository;
    private final ProfileProvider profileProvider;
    private final CardMapper cardMapper;

    @PostMapping("clicks")
    void addClickAction(Integer resourceId) {
        actionService.addClickAction(resourceId);
    }

    @GetMapping("me")
    @PreAuthorize("isAuthenticated()")
    PageActionDTO getPageStats() {
        return actionService.getPageStats();
    }

    @GetMapping("referrals")
    public ReferralStatsDTO getReferralsStats() {
        Account user = profileProvider.getUserFromAuth();
        ReferralStatsDTO res = new ReferralStatsDTO();
        res.setRef1Count(relationRepository.findAllByTypeAndOwner(RelationType.REFERRER, user).size());
        res.setRef2Count(relationRepository.findAllByTypeAndOwner(RelationType.REFERRER_LEVEL2, user).size());
        Instant now = Instant.now();
        Card mainCard = user.getMainCard();
        res.setDayBenefit(actionService.getBenefitBetween(now.minus(Duration.ofDays(1)), now, mainCard));
        res.setWeekBenefit(actionService.getBenefitBetween(now.minus(Duration.ofDays(7)), now, mainCard));
        res.setMonthBenefit(actionService.getBenefitBetween(now.minus(Duration.ofDays(30)), now, mainCard));
        res.setTotalBenefit(actionService.getBenefit(mainCard));
        res.setVisitsCount(actionService.countUniqueVisitsWhereShortnameReferrer(mainCard));
        return res;
    }

    @GetMapping("graph")
    @PreAuthorize("isAuthenticated()")
    List<GraphActionResponse> getWeekGraph() {
        return actionService.getDailyGraph(7);
    }

    @GetMapping("visits-history")
    @PreAuthorize("isAuthenticated()")
    List<BriefCardResponse> getProfilesIVisited() {
        return actionService.getProfilesIVisitedHistory().stream()
                .map(a -> cardMapper.mapToBrief(a.getCard()))
                .collect(Collectors.toList());
    }

}