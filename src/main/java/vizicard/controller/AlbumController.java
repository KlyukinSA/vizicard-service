package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.CloudFileDTO;
import vizicard.model.Card;
import vizicard.model.CloudFile;
import vizicard.model.CloudFileType;
import vizicard.model.TabTypeEnum;
import vizicard.service.AlbumService;
import vizicard.service.CardAttributeService;
import vizicard.service.CloudFileService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cards/{cardAddress}/files")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final CloudFileService cloudFileService;
    private final CardAttributeService cardAttributeService;
    private final ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addUsualFile(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return modelMapper.map(albumService.addFile(card, file, CloudFileType.FILE), CloudFileDTO.class);
    }

    @PostMapping("media")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addMedia(@PathVariable String cardAddress, @RequestPart MultipartFile file) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return modelMapper.map(albumService.addFile(card, file, CloudFileType.MEDIA), CloudFileDTO.class);
    }

    @PostMapping("links")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addLink(@PathVariable String cardAddress, String url) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return modelMapper.map(albumService.addLinkFile(card, url), CloudFileDTO.class);
    }

    @GetMapping
    List<CloudFileDTO> getAllUsualFiles(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.FILES, card);
        return albumService.getAllFiles(card, CloudFileType.FILE).stream()
                .map((val) -> modelMapper.map(val, CloudFileDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("media")
    List<CloudFileDTO> getAllMediaFiles(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.MEDIA, card);
        return albumService.getAllFiles(card, CloudFileType.MEDIA).stream()
                .map((val) -> modelMapper.map(val, CloudFileDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("links")
    List<CloudFileDTO> getAllLinkFiles(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return albumService.getAllFiles(card, CloudFileType.LINK).stream()
                .map((val) -> modelMapper.map(val, CloudFileDTO.class))
                .collect(Collectors.toList());
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteFile(@PathVariable String cardAddress, @PathVariable Integer id) {
        albumService.deleteFile(id);
    }

    @PutMapping("{id}/description")
    public CloudFileDTO updateDescription(@PathVariable String cardAddress, @PathVariable Integer id, @RequestParam String description) {
        CloudFile cloudFile = cloudFileService.updateDescription(id, description);
        return modelMapper.map(cloudFile, CloudFileDTO.class);
    }

    @GetMapping("{id}")
    public CloudFileDTO getFileById(@PathVariable String cardAddress, @PathVariable Integer id) {
        return modelMapper.map(cloudFileService.findById(id), CloudFileDTO.class);
    }

}
