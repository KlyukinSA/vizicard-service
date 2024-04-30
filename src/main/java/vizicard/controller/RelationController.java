package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.profile.response.CardResponse;
import vizicard.dto.profile.request.LeadGenDTO;
import vizicard.dto.RelationResponseDTO;
import vizicard.dto.profile.request.ProfileCreateDTO;
import vizicard.dto.profile.request.ProfileUpdateDTO;
import vizicard.model.Card;
import vizicard.model.Relation;
import vizicard.repository.CardTypeRepository;
import vizicard.service.ProfileService;
import vizicard.service.RelationService;
import vizicard.mapper.CardMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/relations")
@RequiredArgsConstructor
public class RelationController {

    private final RelationService relationService;
    private final CardMapper cardMapper;
    private final ModelMapper modelMapper;
    private final ProfileService profileService;
    private final CardTypeRepository cardTypeRepository;

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public List<RelationResponseDTO> unrelate(@RequestParam Integer cardId) {
        relationService.unrelate(cardId);
        return relationService.getRelationsByAuth()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("save")
    @PreAuthorize("isAuthenticated()")
    public List<RelationResponseDTO> saveContact(@RequestParam Integer id) {
        relationService.saveContact(id);
        return relationService.getRelationsByAuth()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("exchange")
    public void leadGenerate(@RequestParam Integer id, @RequestBody LeadGenDTO dto) {
        Card leadCard = new Card();
        leadCard.setName(dto.getName());
        Card company = new Card();
        company.setName(dto.getCompanyName());
        relationService.leadGenerate(id, leadCard, company, dto.getEmail());
        if (leadCard.getId() != null) {
            profileService.updateProfile(leadCard, modelMapper.map(dto, ProfileUpdateDTO.class));
            profileService.updateCardWithLeadGenCardCreationFields(leadCard, dto);
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
        return new RelationResponseDTO(cardMapper.mapToParamResponse(r.getCard()), r.getCreateAt(), r.getType(), r.getAccountOwner().getId(), r.getCardOwner().getId());
    }

    @PostMapping("card")
    @PreAuthorize("isAuthenticated()")
    public CardResponse createRelationCard(@RequestBody ProfileCreateDTO dto) {
        Card card = new Card();
        card.setName(dto.getName());
        card.setType(cardTypeRepository.findByType(dto.getType()));
        card.setCardName(dto.getCardName());
        relationService.createRelationCard(card);
        profileService.updateProfile(card, modelMapper.map(dto, ProfileUpdateDTO.class)); //
        return cardMapper.mapToResponse(card);
    }

}
