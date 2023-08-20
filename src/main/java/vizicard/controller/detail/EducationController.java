package vizicard.controller.detail;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.ProfileResponseDTO;
import vizicard.dto.detail.EducationDTO;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.service.detail.EducationService;

@RestController
@RequestMapping("users/me/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public EducationResponseDTO createEducation(@RequestBody EducationDTO dto) {
        return educationService.createEducation(dto);
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public EducationResponseDTO updateEducation(@RequestBody EducationDTO dto, @PathVariable("id") Integer id) {
        return educationService.updateEducation(dto, id);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteEducation(@PathVariable("id") Integer id) {
        educationService.deleteEducation(id);
    }

}
