package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.PageActionDTO;
import vizicard.dto.ReferralStatsDTO;
import vizicard.model.Profile;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;
import vizicard.service.ActionService;
import vizicard.utils.ProfileMapper;
import vizicard.utils.ProfileProvider;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    private final RelationRepository relationRepository;
    private final ProfileProvider profileProvider;

    @PostMapping("clicks")
    void addClickAction(@RequestParam Integer id) {
        actionService.addClickAction(id);
    }

    @GetMapping("me")
    @PreAuthorize("isAuthenticated()")
    PageActionDTO getPageStats() {
        return actionService.getPageStats();
    }

//    @PostMapping("ip")
//    public void setAsReferral(Integer id, HttpServletRequest request) {
//        System.out.println(request.getRemoteAddr());
//        Profile user = profileProvider.getUserFromAuth();
//        Profile target = profileProvider.getTarget(id);
//        relationRepository.save(new Relation(user, target, RelationType.REFERRER_LEVEL2));
//        relationRepository.save(new Relation(target, user, RelationType.REFERRAL));
//    }

    @GetMapping("referrals")
    public ReferralStatsDTO getReferralsStats() {
        Profile user = profileProvider.getUserFromAuth();
//        return relationRepository.findAllByProfileAndType(
//                        profileProvider.getUserFromAuth(), RelationType.REFERRAL).stream()
//                .filter(Relation::isStatus)
//                .filter(r -> r.getOwner().isStatus())
//                .map(r -> {
//                    ReferralStatsDTO dto = modelMapper.map(new RelationResponseDTO(
//                            profileMapper.mapToBrief(r.getOwner()),
//                            r.getCreateAt(),
//                            relationRepository.findByOwnerAndProfile(r.getProfile(), r.getOwner()).getType()),
//                            ReferralStatsDTO.class);
//                    dto.setStats(actionService.getProfileStats(r.getOwner()));
//                    return dto;
//                })
//                .collect(Collectors.toList());
        ReferralStatsDTO res = new ReferralStatsDTO();
        res.setRef1Count(relationRepository.findAllByTypeAndOwner(RelationType.REFERRER, user).size());
        res.setRef2Count(relationRepository.findAllByTypeAndOwner(RelationType.REFERRER_LEVEL2, user).size());
        Instant now = Instant.now();
        res.setDayBenefit(actionService.getBenefitBetween(now.minus(Duration.ofDays(1)), now, user));
        res.setWeekBenefit(actionService.getBenefitBetween(now.minus(Duration.ofDays(7)), now, user));
        res.setMonthBenefit(actionService.getBenefitBetween(now.minus(Duration.ofDays(30)), now, user));
        res.setTotalBenefit(actionService.getBenefit(user));
        return res;
    }

}