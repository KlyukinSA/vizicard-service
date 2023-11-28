package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.CloudFileDTO;
import vizicard.model.CloudFile;
import vizicard.model.CloudFileType;
import vizicard.service.AlbumService;
import vizicard.service.CloudFileService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final ModelMapper modelMapper;
    private final CloudFileService cloudFileService;

    @PostMapping("{id}/files")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addUsualFile(@RequestPart MultipartFile file, @PathVariable Integer id) {
        return modelMapper.map(albumService.addFile(file, id, CloudFileType.FILE), CloudFileDTO.class);
    }

    @PostMapping("{id}/files/media")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addMedia(@RequestPart MultipartFile file, @PathVariable Integer id) {
        return modelMapper.map(albumService.addFile(file, id, CloudFileType.MEDIA), CloudFileDTO.class);
    }

    @PostMapping("{id}/files/links")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addLink(@PathVariable Integer id, String url) {
        return modelMapper.map(albumService.addLinkFile(url, id), CloudFileDTO.class);
    }

    @GetMapping("{id}/files")
    @PreAuthorize("isAuthenticated()")
    List<CloudFileDTO> getAllUsualFiles(@PathVariable Integer id) {
        return albumService.getAllFiles(id, CloudFileType.FILE).stream()
                .map((val) -> modelMapper.map(val, CloudFileDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("{id}/files/media")
    @PreAuthorize("isAuthenticated()")
    List<CloudFileDTO> getAllMediaFiles(@PathVariable Integer id) {
        return albumService.getAllFiles(id, CloudFileType.MEDIA).stream()
                .map((val) -> modelMapper.map(val, CloudFileDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("{id}/files/links")
    @PreAuthorize("isAuthenticated()")
    List<CloudFileDTO> getAllLinkFiles(@PathVariable Integer id) {
        return albumService.getAllFiles(id, CloudFileType.LINK).stream()
                .map((val) -> modelMapper.map(val, CloudFileDTO.class))
                .collect(Collectors.toList());
    }

    @DeleteMapping("any/files/{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteFile(@PathVariable Integer id) {
        albumService.deleteFile(id);
    }

    @PutMapping("any/files/{id}/description")
    public CloudFileDTO updateDescription(@PathVariable Integer id, @RequestParam String description) {
        CloudFile cloudFile = cloudFileService.updateDescription(id, description);
        return modelMapper.map(cloudFile, CloudFileDTO.class);
    }
}
