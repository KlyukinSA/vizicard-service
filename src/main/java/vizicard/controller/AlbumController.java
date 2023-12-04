package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.CloudFileDTO;
import vizicard.model.*;
import vizicard.repository.CardRepository;
import vizicard.service.AlbumService;
import vizicard.service.CardAttributeService;
import vizicard.service.CloudFileService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cards/{cardAddress}")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final CloudFileService cloudFileService;
    private final CardAttributeService cardAttributeService;
    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;

    @PostMapping("files")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addUsualFile(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return getCloudFileDTO(albumService.addFile(card, file, CloudFileType.FILE));
    }

    private CloudFileDTO getCloudFileDTO(CloudFile cloudFile) {
        Extension extension = cloudFile.getExtension();
        cloudFile.setExtension(null);
        CloudFileDTO res = modelMapper.map(cloudFile, CloudFileDTO.class);
        res.setExtension(extension.getName());
        res.setColor(extension.getColor());
        cloudFile.setExtension(extension);
        return res;
    }

    @PostMapping("medias")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addMedia(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return getCloudFileDTO(albumService.addFile(card, file, CloudFileType.MEDIA));
    }

    @PostMapping("links")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addLink(@PathVariable String cardAddress, String url) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return getCloudFileDTO(albumService.addLinkFile(card, url));
    }

    @GetMapping("files")
    List<CloudFileDTO> getAllUsualFiles(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.FILES, card);
        return albumService.getAllFiles(card, CloudFileType.FILE).stream()
                .map(this::getCloudFileDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("medias")
    List<CloudFileDTO> getAllMediaFiles(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.MEDIAS, card);
        return albumService.getAllFiles(card, CloudFileType.MEDIA).stream()
                .map(this::getCloudFileDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("links")
    List<CloudFileDTO> getAllLinkFiles(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return albumService.getAllFiles(card, CloudFileType.LINK).stream()
                .map(this::getCloudFileDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("files/{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteFile(@PathVariable String cardAddress, @PathVariable Integer id) {
        albumService.deleteFile(id);
    }

    @PutMapping("files/{id}/description")
    public CloudFileDTO updateDescription(@PathVariable String cardAddress, @PathVariable Integer id, @RequestParam String description) {
        CloudFile cloudFile = cloudFileService.updateDescription(id, description);
        return getCloudFileDTO(cloudFile);
    }

    @GetMapping("files/{id}")
    public CloudFileDTO getFileById(@PathVariable String cardAddress, @PathVariable Integer id) {
        return getCloudFileDTO(cloudFileService.findById(id));
    }

    @PutMapping("avatar")
    @PreAuthorize("isAuthenticated()")
    public CloudFileDTO updateAvatar(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        CloudFile cloudFile = albumService.addFile(card, file, CloudFileType.MEDIA);
        card.setAvatarId(cloudFile.getId());
        cardRepository.save(card);
        return getCloudFileDTO(cloudFile);
    }

    @PutMapping("background")
    @PreAuthorize("isAuthenticated()")
    public CloudFileDTO updateBackground(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        CloudFile cloudFile = albumService.addFile(card, file, CloudFileType.MEDIA);
        card.setBackgroundId(cloudFile.getId());
        cardRepository.save(card);
        return getCloudFileDTO(cloudFile);
    }

}
