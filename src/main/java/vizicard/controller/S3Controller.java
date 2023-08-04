package vizicard.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.UserResponseDTO;
import vizicard.service.S3Service;

import java.io.IOException;

@RestController
@RequestMapping("/files")
@Api(tags = "files")
@RequiredArgsConstructor
public class S3Controller {
    // https://www.baeldung.com/aws-s3-java
    // https://github.com/davidarchanjo/spring-boot-aws-s3/blob/main/src/main/java/br/com/example/davidarchanjo/controller/S3BucketStorageController.java

    private final S3Service service;

    @PostMapping("upload")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<?> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("fileName") String fileName
    ) throws IOException {
        service.uploadFile(fileName, file.getSize(), file.getContentType(), file.getInputStream());
        return ResponseEntity.ok().build();
    }

}
