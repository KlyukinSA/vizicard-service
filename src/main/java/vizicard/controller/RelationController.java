package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.contact.ContactInListRequest;
import vizicard.dto.profile.LeadGenDTO;
import vizicard.dto.RelationResponseDTO;
import vizicard.dto.profile.ProfileUpdateDTO;
import vizicard.model.Card;
import vizicard.model.ContactEnum;
import vizicard.model.Relation;
import vizicard.service.ProfileService;
import vizicard.service.RelationService;
import vizicard.mapper.CardMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/relations")
@RequiredArgsConstructor
public class RelationController {

    private final RelationService relationService;
    private final CardMapper cardMapper;
    private final ModelMapper modelMapper;
    private final ProfileService profileService;

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public void unrelate(@RequestParam Integer cardId) {
        relationService.unrelate(cardId);
    }

    @PostMapping
    public ResponseEntity<?> saveContact(@RequestParam Integer id) throws Exception {
        return relationService.saveContact(id);
    }

    @PostMapping("/lead")
    public void leadGenerate(@RequestParam Integer id, @RequestBody(required = false) LeadGenDTO dto) {
        Card leadCard = new Card();
        leadCard.setName(dto.getName());
        Card company = new Card();
        company.setName(dto.getName());
        ContactInListRequest contactInListRequest = dto.getContacts().stream().filter(req -> req.getType().equals(ContactEnum.MAIL)).findFirst().orElse(new ContactInListRequest());
        relationService.leadGenerate(id, leadCard, company, contactInListRequest.getContact());
        if (leadCard.getId() != null) {
            profileService.updateProfile(leadCard, modelMapper.map(dto, ProfileUpdateDTO.class));
        }
    }

    @GetMapping
    public List<RelationResponseDTO> searchLike(@RequestParam(required = false) String name, @RequestParam(required = false) String type) {
        return relationService.searchLike(name, type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("referrals")
    public List<RelationResponseDTO> getReferralsWithLevelOrAll(@RequestParam(required = false) Integer level) {
        return relationService.getReferralsWithLevelOrAll(level).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RelationResponseDTO mapToResponse(Relation r) {
        return new RelationResponseDTO(cardMapper.mapToBrief(r.getCard()), r.getCreateAt(), r.getType(), r.getAccountOwner().getId(), r.getCardOwner().getId());
    }

}
