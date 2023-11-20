package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.SkillDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.model.detail.Skill;
import vizicard.service.detail.SkillService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("profiles/me/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService service;
    private final ModelMapper modelMapper;

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public List<SkillResponseDTO> changeSkills(@RequestBody SkillDTO dto) {
        return service.changeSkills(dto).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<SkillResponseDTO> getOfCurrentCard() {
        return service.getOfCurrentCard()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SkillResponseDTO mapToResponse(Skill detail) {
        SkillResponseDTO res = modelMapper.map(detail, SkillResponseDTO.class);
        res.setId(detail.getIndividualId());
        return res;
    }

}
