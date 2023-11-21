package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.SkillDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.mapper.DetailResponseMapper;
import vizicard.model.detail.Skill;
import vizicard.service.detail.SkillService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("profiles/me/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService service;
    private final DetailResponseMapper mapper;

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public List<SkillResponseDTO> changeSkills(@RequestBody SkillDTO dto) {
        return service.changeSkills(dto).stream()
                .map(mapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<SkillResponseDTO> getOfCurrentCard() {
        return service.getOfCurrentCard()
                .map(mapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    public SkillResponseDTO create(@RequestBody String s) {
        return mapper.mapToResponse(service.create(s));
    }

    @DeleteMapping("{id}")
    public void delete(@RequestParam Integer id) {
        service.delete(id);
    }

}
