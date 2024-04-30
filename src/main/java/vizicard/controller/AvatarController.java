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

import java.util.List;
import java.util.stream.Collectors;

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
    public List<String> updateAvatar(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        card = cardService.prepareToUpdate(card);
        List<CloudFile> cloudFiles = albumService.addScaledPhotos(card, file, 3);
        card.setAvatarId(cloudFiles.get(0).getId());
        cardRepository.save(card);
        return cloudFiles.stream()
                .map(CloudFile::getUrl)
                .collect(Collectors.toList());
    }

    @PutMapping("background")
    @PreAuthorize("isAuthenticated()")
    public List<String> updateBackground(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        card = cardService.prepareToUpdate(card);
        List<CloudFile> cloudFiles = albumService.addScaledPhotos(card, file, 3);
        card.setBackgroundId(cloudFiles.get(0).getId());
        cardRepository.save(card);
        return cloudFiles.stream()
                .map(CloudFile::getUrl)
                .collect(Collectors.toList());
    }

}
