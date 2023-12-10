package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.CloudFileDTO;
import vizicard.mapper.CloudFileMapper;
import vizicard.model.*;
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
    private final CloudFileMapper cloudFileMapper;

    @PostMapping("files")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addUsualFile(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return cloudFileMapper.mapToDTO(albumService.addFile(card, file, CloudFileType.FILE));
    }

    @PostMapping("medias")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addMedia(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return cloudFileMapper.mapToDTO(albumService.addFile(card, file, CloudFileType.MEDIA));
    }

    @PostMapping("links")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addLink(@PathVariable String cardAddress, String url) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return cloudFileMapper.mapToDTO(albumService.addLinkFile(card, url));
    }

    @GetMapping("files")
    List<CloudFileDTO> getAllUsualFiles(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.FILES, card);
        return albumService.getAllFiles(card, CloudFileType.FILE).stream()
                .map(cloudFileMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("medias")
    List<CloudFileDTO> getAllMediaFiles(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.MEDIAS, card);
        return albumService.getAllFiles(card, CloudFileType.MEDIA).stream()
                .map(cloudFileMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("links")
    List<CloudFileDTO> getAllLinkFiles(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return albumService.getAllFiles(card, CloudFileType.LINK).stream()
                .map(cloudFileMapper::mapToDTO)
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
        return cloudFileMapper.mapToDTO(cloudFile);
    }

    @GetMapping("files/{id}")
    public CloudFileDTO getFileById(@PathVariable String cardAddress, @PathVariable Integer id) {
        return cloudFileMapper.mapToDTO(cloudFileService.findById(id));
    }

}
