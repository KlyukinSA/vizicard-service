package vizicard.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.ProfileResponseDTO;
import vizicard.model.CloudFile;
import vizicard.service.S3Service;

import java.io.IOException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service service;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public CloudFile uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
        return service.uploadFile(file);
    }

    @GetMapping("{id}")
    public String getUrlById(@RequestParam("id") Integer id) {
        return service.getUrlById(id);
    }

}
