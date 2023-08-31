package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.SkillDTO;
import vizicard.service.detail.SkillService;

import java.util.List;

@RestController
@RequestMapping("profiles/me/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService service;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public void addSkills(@RequestBody List<SkillDTO> dto) {
        service.addSkills(dto);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteEducation(@PathVariable("id") Integer id) {
        service.deleteSkill(id);
    }

}
