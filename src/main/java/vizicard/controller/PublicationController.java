package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.PublicationDTO;
import vizicard.dto.detail.EducationDTO;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.service.PublicationService;

import java.util.List;

@RestController
@RequestMapping("/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public PublicationDTO createPublication(@RequestBody PublicationDTO dto) {
        return publicationService.createPublication(dto);
    }

    @GetMapping("my")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationDTO> getAllMy() {
        return publicationService.getAllMy();
    }

    @GetMapping("my-on-page")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationDTO> getAllMyOnPage(@RequestParam Integer id) {
        return publicationService.getAllMyOnPage(id);
    }

}
