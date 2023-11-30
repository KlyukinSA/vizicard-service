package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.SkillDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.mapper.DetailResponseMapper;
import vizicard.model.Card;
import vizicard.model.TabTypeEnum;
import vizicard.service.CardAttributeService;
import vizicard.service.detail.SkillService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cards/{cardAddress}/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService service;
    private final CardAttributeService cardAttributeService;
    private final DetailResponseMapper mapper;

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public List<SkillResponseDTO> changeSkills(@PathVariable String cardAddress, @RequestBody SkillDTO dto) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return service.changeSkills(card, dto).stream()
                .map(mapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<SkillResponseDTO> getAllOfCard(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.RESUME, card);
        return service.getAllOfCard(card)
                .map(mapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public SkillResponseDTO create(@PathVariable String cardAddress, @RequestBody String s) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return mapper.mapToResponse(service.create(card, s));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void delete(@PathVariable String cardAddress, @PathVariable Integer id) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        service.delete(card, id);
    }

    @GetMapping("{id}")
    public SkillResponseDTO getById(@PathVariable String cardAddress, @PathVariable Integer id) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.RESUME, card);
        return mapper.mapToResponse(service.findById(card, id));
    }

}
