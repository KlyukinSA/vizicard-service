package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.CloudFileDTO;
import vizicard.service.AlbumService;
import vizicard.service.ContactService;

import java.io.IOException;

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

}
