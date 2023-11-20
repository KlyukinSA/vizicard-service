package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.CardTypeDTO;
import vizicard.dto.profile.response.CardResponse;
import vizicard.dto.profile.request.ProfileCreateDTO;
import vizicard.dto.profile.request.ProfileUpdateDTO;
import vizicard.dto.profile.response.IdAndTypeAndMainShortnameDTO;
import vizicard.dto.profile.response.ParamCardResponse;
import vizicard.exception.CustomException;
import vizicard.model.Card;
import vizicard.model.CardTypeEnum;
import vizicard.repository.CardTypeRepository;
import vizicard.service.CardService;
import vizicard.service.ProfileService;
import vizicard.mapper.CardMapper;
import vizicard.service.ShortnameService;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final CardMapper cardMapper;

    private final ProfileService profileService;
    private final ModelMapper modelMapper;
    private final ShortnameService shortnameService;
    private final ProfileProvider profileProvider;
    private final CardTypeRepository cardTypeRepository;

    @GetMapping("{shortname}")
    public CardResponse searchByShortname(@PathVariable String shortname) {
        return cardMapper.mapToResponse(cardService.searchByShortname(shortname));
    }

    @GetMapping("id{id}")
    public CardResponse searchById(@PathVariable Integer id) {
        return cardMapper.mapToResponse(cardService.searchById(id));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void delete(@PathVariable Integer id) {
        cardService.delete(id);
    }

    @PutMapping("id{id}")
    @PreAuthorize("isAuthenticated()")
    public CardResponse updateById(@PathVariable Integer id, @RequestBody ProfileUpdateDTO dto) {
        Card card = cardService.prepareToUpdate(id); // TODO update(Card sourceMap, List<Contact> newContacts, CloudFile avatar, Integer destId)
        profileService.updateProfile(card, dto); //
        return cardMapper.mapToResponse(profileProvider.getTarget(id));
    }

    @PutMapping("{shortname}")
    @PreAuthorize("isAuthenticated()")
    public CardResponse updateByShortname(@PathVariable String shortname, @RequestBody ProfileUpdateDTO dto) {
        Card card = cardService.prepareToUpdate(shortname); // TODO update(Card sourceMap, List<Contact> newContacts, CloudFile avatar, Integer destId)
        profileService.updateProfile(card, dto); //
        return cardMapper.mapToResponse(profileProvider.getTarget(card.getId()));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public CardResponse createMyCard(@RequestBody ProfileCreateDTO dto) {
        if (dto.getType() != CardTypeEnum.PERSON) {
            throw new CustomException("create not person as relation card", HttpStatus.BAD_REQUEST);
        }
        Card card = new Card();
        card.setName(dto.getName());
        card.setType(cardTypeRepository.findByType(dto.getType()));
        card.setCustom(false);
        card.setCardName(dto.getCardName());
        cardService.createMyCard(card);
        profileService.updateProfile(card, modelMapper.map(dto, ProfileUpdateDTO.class)); //
        return cardMapper.mapToResponse(card);
    }

    @GetMapping("current")
    @PreAuthorize("isAuthenticated()")
    public CardResponse whoami() {
        return cardMapper.mapToResponse(cardService.whoami());
    }

    @GetMapping("my")
    @PreAuthorize("isAuthenticated()")
    public List<ParamCardResponse> getAllMyCards() {
        return cardService.getAllMyCards().stream()
                .map(cardMapper::mapToParamResponse)
                .collect(Collectors.toList());
    }

    @PutMapping("merge")
    @PreAuthorize("isAuthenticated()")
    public CardResponse mergeCustomCards(@RequestParam Integer main, @RequestParam Integer secondary) {
        return cardMapper.mapToResponse(profileService.mergeCustomProfiles(main, secondary)); //
    }

    @GetMapping("id-type-shortname")
    @PreAuthorize("isAuthenticated()")
    public IdAndTypeAndMainShortnameDTO getIdAndTypeAndMainShortnameOfCurrentCard() {
        Card card = cardService.whoami();
        return new IdAndTypeAndMainShortnameDTO(
                card.getId(),
                modelMapper.map(cardTypeRepository.findByType(card.getType().getType()), CardTypeDTO.class),
                shortnameService.getMainShortname(card));
    }

    @GetMapping("types")
    public List<CardTypeDTO> getAllTypes() {
        return cardTypeRepository.findAll().stream()
                .map(t -> modelMapper.map(t, CardTypeDTO.class))
                .collect(Collectors.toList());
    }

}
