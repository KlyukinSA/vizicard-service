package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vizicard.dto.contact.FullContactResponse;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.exception.CustomException;
import vizicard.mapper.ContactMapper;
import vizicard.mapper.DetailResponseMapper;
import vizicard.model.*;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;
import vizicard.model.detail.ProfileDetailStruct;
import vizicard.model.detail.Skill;
import vizicard.repository.RelationRepository;
import vizicard.repository.TabRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cards/{id}")
@RequiredArgsConstructor
public class CardAttributeController {

    private final TabRepository tabRepository;
    private final RelationRepository relationRepository;
    private final ProfileProvider profileProvider;

    private final ContactMapper contactMapper;
    private final DetailResponseMapper detailResponseMapper;


    @GetMapping("contacts")
    @PreAuthorize("isAuthenticated()")
    public List<FullContactResponse> getContacts(@PathVariable Integer id) {
        Card card = profileProvider.getTarget(id);
        List<Contact> list = (List<Contact>) getListThroughOverlay(card, Card::getContacts);
        stopAccessToHiddenOrEmptyTab(TabTypeEnum.CONTACTS, list, card);
        return contactMapper.mapList(list);
    }

    private List<? extends CardAttribute> getListThroughOverlay(Card card, Function<Card, List<? extends CardAttribute>> f) {
        List<? extends CardAttribute> list = f.apply(card);
        Relation relation = relationRepository.findByAccountOwnerAndCard(profileProvider.getUserFromAuth(), card);
        if (relation != null) {
            Card overlay = relation.getOverlay();
            if (overlay != null) {
                List<? extends CardAttribute> list1 = f.apply(overlay);
                if (list1 != null) {
                    list = list1;
                }
            }
        }
        return list;
    }

    @GetMapping("education")
    public List<EducationResponseDTO> getEducation(@PathVariable Integer id) {
        Card card = profileProvider.getTarget(id);
        List<Education> list = card.getDetailStruct().getEducation();
        stopAccessToHiddenOrEmptyTab(TabTypeEnum.RESUME, list, card);
        return list.stream()
                .filter(Education::isStatus)
                .map(detailResponseMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("experience")
    public List<ExperienceResponseDTO> getExperience(@PathVariable Integer id) {
        Card card = profileProvider.getTarget(id);
        List<Experience> list = card.getDetailStruct().getExperience();
        stopAccessToHiddenOrEmptyTab(TabTypeEnum.RESUME, list, card);
        return list.stream()
                .filter(Experience::isStatus)
                .map(detailResponseMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("skills")
    public List<SkillResponseDTO> getSkills(@PathVariable Integer id) {
        Card card = profileProvider.getTarget(id);
        List<Skill> list = card.getDetailStruct().getSkills();
        stopAccessToHiddenOrEmptyTab(TabTypeEnum.RESUME, list, card);
        return list.stream()
                .filter(Skill::isStatus)
                .map(detailResponseMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    private void stopAccessToHiddenOrEmptyTab(TabTypeEnum tabType, List<? extends CardAttribute> list, Card card) {
        Account user = profileProvider.getUserFromAuth();
        boolean isCurrentCard = user != null && user.getCurrentCard().getId().equals(card.getId()); // TODO same in CardMapper
        Optional<Tab> optionalTab = tabRepository.findByTypeTypeAndCardOwner(tabType, card);
        if ((optionalTab.isPresent() && optionalTab.get().isHidden()
                && !isCurrentCard) || (list != null && list.isEmpty())) {
            throw new CustomException("nothing here", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("resume")
    public ProfileDetailStructResponseDTO getAllResume(@PathVariable Integer id) {
        Card card = profileProvider.getTarget(id);
        stopAccessToHiddenOrEmptyTab(TabTypeEnum.RESUME, null, card); // Any
        ProfileDetailStruct detailStruct = card.getDetailStruct();
        return new ProfileDetailStructResponseDTO(
                detailStruct.getEducation().stream()
                        .filter(CardAttribute::isStatus)
                        .map(detailResponseMapper::mapToResponse)
                        .collect(Collectors.toList()),
                detailStruct.getExperience().stream()
                        .filter(CardAttribute::isStatus)
                        .map(detailResponseMapper::mapToResponse)
                        .collect(Collectors.toList()),
                detailStruct.getSkills().stream()
                        .filter(CardAttribute::isStatus)
                        .map(detailResponseMapper::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

}
