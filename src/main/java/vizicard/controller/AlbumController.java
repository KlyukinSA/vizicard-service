package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.CloudFileDTO;
import vizicard.service.AlbumService;
import vizicard.service.ContactService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final ModelMapper modelMapper;

    @PostMapping("{id}/files")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addFile(@RequestPart MultipartFile file, @PathVariable Integer id) throws IOException {
        return modelMapper.map(albumService.addFile(file, id), CloudFileDTO.class);
    }

    @GetMapping("{id}/files")
    @PreAuthorize("isAuthenticated()")
    List<CloudFileDTO> getAllFiles(@PathVariable Integer id) {
        return albumService.getAllFiles(id).stream()
                .map((val) -> modelMapper.map(val, CloudFileDTO.class))
                .collect(Collectors.toList());
    }

    @DeleteMapping("any/files/{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteFile(@PathVariable Integer id) {
        albumService.deleteFile(id);
    }

}
