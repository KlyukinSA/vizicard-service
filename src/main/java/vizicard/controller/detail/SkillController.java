package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.ExperienceDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.SkillDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.service.detail.ExperienceService;
import vizicard.service.detail.SkillService;

@RestController
@RequestMapping("profiles/me/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService service;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public SkillResponseDTO createEducation(@RequestBody SkillDTO dto) {
        return service.createSkill(dto);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteEducation(@PathVariable("id") Integer id) {
        service.deleteSkill(id);
    }

}
