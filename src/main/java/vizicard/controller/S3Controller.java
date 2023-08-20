package vizicard.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vizicard.service.S3Service;

@RestController
@RequestMapping("/files")
@Api(tags = "files")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service service;

//    @PostMapping("upload")
////    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
//        return ResponseEntity.ok().body(service.uploadFile(file));
//    }

    @GetMapping("{id}")
    public String getUrlById(@RequestParam("id") Integer id) {
        return service.getUrlById(id);
    }

}
