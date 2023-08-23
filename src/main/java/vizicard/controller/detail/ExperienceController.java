package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.EducationDTO;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.service.detail.EducationService;
import vizicard.service.detail.ExperienceService;

@RestController
@RequestMapping("profiles/me/experience")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService service;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ExperienceResponseDTO createEducation(@RequestBody ExperienceDTO dto) {
        return service.createExperience(dto);
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ExperienceResponseDTO updateEducation(@RequestBody ExperienceDTO dto, @PathVariable("id") Integer id) {
        return service.updateExperience(dto, id);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteEducation(@PathVariable("id") Integer id) {
        service.deleteExperience(id);
    }

}
