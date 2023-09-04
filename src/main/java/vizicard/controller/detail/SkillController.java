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

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public void changeSkills(@RequestBody SkillDTO dto) {
        service.changeSkills(dto);
    }

}
