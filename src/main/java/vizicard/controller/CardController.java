package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.profile.response.BriefCardResponse;
import vizicard.dto.profile.response.CardResponse;
import vizicard.dto.profile.request.ProfileCreateDTO;
import vizicard.dto.profile.request.ProfileUpdateDTO;
import vizicard.model.Card;
import vizicard.service.CardService;
import vizicard.service.ProfileService;
import vizicard.mapper.CardMapper;

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

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public CardResponse update(@PathVariable Integer id, @RequestBody ProfileUpdateDTO dto) {
        Card card = cardService.prepareToUpdate(id); // TODO create update(Card sourceMap, Integer destId)
        profileService.updateProfile(card, dto); //
        return cardMapper.mapToResponse(card);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public CardResponse createMyCard(@RequestBody ProfileCreateDTO dto) {
        Card card = new Card();
        card.setName(dto.getName());
        card.setType(dto.getType());
        card.setCustom(false);
        cardService.createMyCard(card);
        profileService.updateProfile(card, modelMapper.map(dto, ProfileUpdateDTO.class)); //
        return cardMapper.mapToResponse(card);
    }

    @GetMapping("current")
    @PreAuthorize("isAuthenticated()")
    public CardResponse whoami() {
        return cardMapper.mapToResponse(cardService.whoami());
    }

    @GetMapping("shortname-and-id")
    @PreAuthorize("isAuthenticated()")
    public CardResponse getShortInfoAboutCurrentCard() {
        return cardMapper.mapToResponse(cardService.whoami());
    }

    @GetMapping("my")
    @PreAuthorize("isAuthenticated()")
    public List<BriefCardResponse> getAllMyCards() {
        return cardService.getAllMyCards().stream()
                .map(cardMapper::mapToBrief)
                .collect(Collectors.toList());
    }

    @PutMapping("merge")
    @PreAuthorize("isAuthenticated()")
    public CardResponse mergeCustomCards(@RequestParam Integer main, @RequestParam Integer secondary) {
        return cardMapper.mapToResponse(profileService.mergeCustomProfiles(main, secondary)); //
    }

}
