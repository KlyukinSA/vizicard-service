package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.CloudFileDTO;
import vizicard.mapper.CloudFileMapper;
import vizicard.model.Card;
import vizicard.model.CloudFile;
import vizicard.model.CloudFileType;
import vizicard.repository.CardRepository;
import vizicard.service.AlbumService;
import vizicard.service.CardAttributeService;
import vizicard.service.CardService;

@RestController
@RequestMapping("cards/{cardAddress}")
@RequiredArgsConstructor
public class AvatarController {

    private final CardRepository cardRepository;
    private final CardService cardService;
    private final CardAttributeService cardAttributeService;
    private final AlbumService albumService;
    private final CloudFileMapper cloudFileMapper;

    @PutMapping("avatar")
    @PreAuthorize("isAuthenticated()")
    public CloudFileDTO updateAvatar(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        card = cardService.prepareToUpdate(card);
        CloudFile cloudFile = albumService.addFile(card, file, CloudFileType.MEDIA);
        card.setAvatarId(cloudFile.getId());
        cardRepository.save(card);
        return cloudFileMapper.mapToDTO(cloudFile);
    }

    @PutMapping("background")
    @PreAuthorize("isAuthenticated()")
    public CloudFileDTO updateBackground(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        card = cardService.prepareToUpdate(card);
        CloudFile cloudFile = albumService.addFile(card, file, CloudFileType.MEDIA);
        card.setBackgroundId(cloudFile.getId());
        cardRepository.save(card);
        return cloudFileMapper.mapToDTO(cloudFile);
    }

}
