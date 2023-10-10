package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.action.GraphActionResponse;
import vizicard.dto.action.PageActionDTO;
import vizicard.dto.action.ReferralStatsDTO;
import vizicard.model.ContactEnum;
import vizicard.model.Profile;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;
import vizicard.service.ActionService;
import vizicard.utils.ProfileProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    private final RelationRepository relationRepository;
    private final ProfileProvider profileProvider;

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
        Profile user = profileProvider.getUserFromAuth();
        ReferralStatsDTO res = new ReferralStatsDTO();
        res.setRef1Count(relationRepository.findAllByTypeAndOwner(RelationType.REFERRER, user).size());
        res.setRef2Count(relationRepository.findAllByTypeAndOwner(RelationType.REFERRER_LEVEL2, user).size());
        Instant now = Instant.now();
        res.setDayBenefit(actionService.getBenefitBetween(now.minus(Duration.ofDays(1)), now, user));
        res.setWeekBenefit(actionService.getBenefitBetween(now.minus(Duration.ofDays(7)), now, user));
        res.setMonthBenefit(actionService.getBenefitBetween(now.minus(Duration.ofDays(30)), now, user));
        res.setTotalBenefit(actionService.getBenefit(user));
        res.setVisitsCount(actionService.countUniqueVisitsWhereShortnameReferrer(user));
        return res;
    }

    @GetMapping("graph")
    @PreAuthorize("isAuthenticated()")
    List<GraphActionResponse> getWeekGraph() {
        return actionService.getDailyGraph(7);
    }

}