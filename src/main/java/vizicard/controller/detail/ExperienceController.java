package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.ExperienceDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.mapper.DetailResponseMapper;
import vizicard.model.Card;
import vizicard.model.TabTypeEnum;
import vizicard.service.CardAttributeService;
import vizicard.service.detail.ExperienceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cards/{cardAddress}/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService service;
    private final CardAttributeService cardAttributeService;
    private final DetailResponseMapper mapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ExperienceResponseDTO createExperience(@PathVariable String cardAddress, @RequestBody ExperienceDTO dto) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return mapper.mapToResponse(service.createExperience(card, dto));
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ExperienceResponseDTO updateExperience(@PathVariable String cardAddress, @RequestBody ExperienceDTO dto, @PathVariable("id") Integer id) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return mapper.mapToResponse(service.updateExperience(card, dto, id));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteExperience(@PathVariable String cardAddress, @PathVariable("id") Integer id) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        service.deleteExperience(card, id);
    }

    @GetMapping
    public List<ExperienceResponseDTO> getAllOfCard(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.RESUME, card);
        return service.getAllOfCard(card)
                .map(mapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public ExperienceResponseDTO getById(@PathVariable String cardAddress, @PathVariable Integer id) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.RESUME, card);
        return mapper.mapToResponse(service.findById(card, id));
    }

}
