package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.EducationDTO;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.EducationTypeDTO;
import vizicard.service.detail.EducationService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("profiles/me/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRO')")
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

    @GetMapping("types")
    public List<EducationTypeDTO> findAllTypes() {
        return educationService.findAllTypes();
    }

}
