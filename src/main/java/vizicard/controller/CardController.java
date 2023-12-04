package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.CardTypeDTO;
import vizicard.dto.QRCodeResponse;
import vizicard.dto.detail.ResumeResponseDTO;
import vizicard.dto.profile.response.CardResponse;
import vizicard.dto.profile.request.ProfileCreateDTO;
import vizicard.dto.profile.request.ProfileUpdateDTO;
import vizicard.dto.profile.response.IdAndTypeAndMainShortnameDTO;
import vizicard.dto.profile.response.MainResponseDTO;
import vizicard.dto.profile.response.ParamCardResponse;
import vizicard.exception.CustomException;
import vizicard.mapper.DetailResponseMapper;
import vizicard.model.Card;
import vizicard.model.CardAttribute;
import vizicard.model.CardTypeEnum;
import vizicard.model.TabTypeEnum;
import vizicard.model.detail.ProfileDetailStruct;
import vizicard.repository.CardTypeRepository;
import vizicard.service.*;
import vizicard.mapper.CardMapper;
import vizicard.utils.ProfileProvider;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cards/{cardAddress}")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final ProfileService profileService;
    private final ShortnameService shortnameService;
    private final ProfileProvider profileProvider;
    private final CardTypeRepository cardTypeRepository;
    private final CardAttributeService cardAttributeService; // TODO create AddressService
    private final QRService qrService;

    private final CardMapper cardMapper;
    private final ModelMapper modelMapper;

    @GetMapping
    public CardResponse search(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return cardMapper.mapToResponse(cardService.search(card));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public void delete(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardService.delete(card);
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public MainResponseDTO update(@PathVariable String cardAddress, @RequestBody ProfileUpdateDTO dto) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardService.prepareToUpdate(card);
        profileService.updateProfile(card, dto); //
        return cardMapper.mapToMainResponse(profileProvider.getTarget(card.getId()));
    }

    @PostMapping // TODO move to AccountController
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

    @GetMapping("current") // TODO move to AccountController
    @PreAuthorize("isAuthenticated()")
    public CardResponse whoami() {
        return cardMapper.mapToResponse(cardService.whoami());
    }

    @GetMapping("cards") // TODO move to AccountController
    @PreAuthorize("isAuthenticated()")
    public List<ParamCardResponse> getAllMyCards() {
        return cardService.getAllMyCards().stream()
                .map(cardMapper::mapToParamResponse)
                .collect(Collectors.toList());
    }

    @PutMapping("merge") // TODO remove
    @PreAuthorize("isAuthenticated()")
    public CardResponse mergeCustomCards(@RequestParam Integer main, @RequestParam Integer secondary) {
        return cardMapper.mapToResponse(profileService.mergeCustomProfiles(main, secondary)); //
    }

    @GetMapping("id-type-shortname")
    @PreAuthorize("isAuthenticated()")
    public IdAndTypeAndMainShortnameDTO getIdAndTypeAndMainShortnameOfCard(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return new IdAndTypeAndMainShortnameDTO(
                card.getId(),
                modelMapper.map(cardTypeRepository.findByType(card.getType().getType()), CardTypeDTO.class),
                shortnameService.getMainShortname(card));
    }

    @GetMapping("types") // TODO remove
    public List<CardTypeDTO> getAllTypes() {
        return cardTypeRepository.findAll().stream()
                .map(t -> modelMapper.map(t, CardTypeDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("resume")
    public ResumeResponseDTO getResume(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.RESUME, card);
        return cardMapper.mapToResume(card);
    }

    @GetMapping("qr")
    public QRCodeResponse generateQRCodeForMainShortname(@PathVariable String cardAddress) throws IOException, InterruptedException {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        String mainShortname = shortnameService.getMainShortname(card);
        return new QRCodeResponse(qrService.generate(mainShortname), mainShortname);
    }

}
