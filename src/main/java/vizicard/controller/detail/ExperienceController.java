package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.EducationDTO;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.model.detail.Experience;
import vizicard.service.detail.EducationService;
import vizicard.service.detail.ExperienceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("profiles/me/experience")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService service;
    private final ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ExperienceResponseDTO createExperience(@RequestBody ExperienceDTO dto) {
        return mapToResponse(service.createExperience(dto));
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ExperienceResponseDTO updateExperience(@RequestBody ExperienceDTO dto, @PathVariable("id") Integer id) {
        return mapToResponse(service.updateExperience(dto, id));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteExperience(@PathVariable("id") Integer id) {
        service.deleteExperience(id);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ExperienceResponseDTO> getOfCurrentCard() {
        return service.getOfCurrentCard()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ExperienceResponseDTO mapToResponse(Experience experience) {
        ExperienceResponseDTO res = modelMapper.map(experience, ExperienceResponseDTO.class);
        res.setId(experience.getIndividualId());
        return res;
    }

}
