package vizicard.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.CloudFileDTO;
import vizicard.dto.ProfileResponseDTO;
import vizicard.model.CloudFile;
import vizicard.service.S3Service;

import java.io.IOException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service service;
    private final ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public CloudFileDTO uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
        return modelMapper.map(service.uploadFile(file), CloudFileDTO.class);
    }

    @GetMapping("{id}")
    public String getUrlById(@RequestParam("id") Integer id) {
        return service.getUrlById(id);
    }

}
