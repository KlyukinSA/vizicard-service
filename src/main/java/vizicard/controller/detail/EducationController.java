package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.*;
import vizicard.mapper.DetailResponseMapper;
import vizicard.model.Card;
import vizicard.model.TabTypeEnum;
import vizicard.service.CardAttributeService;
import vizicard.service.detail.EducationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cards/{cardAddress}/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final CardAttributeService cardAttributeService;
    private final DetailResponseMapper mapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public EducationResponseDTO createEducation(@PathVariable String cardAddress, @RequestBody EducationDTO dto) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return mapper.mapToResponse(educationService.createEducation(card, dto));
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public EducationResponseDTO updateEducation(@PathVariable String cardAddress, @RequestBody EducationDTO dto, @PathVariable("id") Integer id) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return mapper.mapToResponse(educationService.updateEducation(card, dto, id));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteEducation(@PathVariable String cardAddress, @PathVariable("id") Integer id) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        educationService.deleteEducation(card, id);
    }

    @GetMapping("types")
    public List<EducationTypeDTO> findAllTypes() {
        return educationService.findAllTypes();
    }

    @GetMapping
    public List<EducationResponseDTO> getAllOfCard(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.RESUME, card);
        return educationService.getAllOfCard(card)
                .map(mapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public EducationResponseDTO getById(@PathVariable String cardAddress, @PathVariable Integer id) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.RESUME, card);
        return mapper.mapToResponse(educationService.findById(card, id));
    }

}
