package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.*;
import vizicard.mapper.DetailResponseMapper;
import vizicard.model.detail.Education;
import vizicard.service.detail.EducationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("profiles/me/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final DetailResponseMapper mapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public EducationResponseDTO createEducation(@RequestBody EducationDTO dto) {
        return mapper.mapToResponse(educationService.createEducation(dto));
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public EducationResponseDTO updateEducation(@RequestBody EducationDTO dto, @PathVariable("id") Integer id) {
        return mapper.mapToResponse(educationService.updateEducation(dto, id));
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

    @GetMapping
    public List<EducationResponseDTO> getOfCurrentCard() {
        return educationService.getOfCurrentCard()
                .map(mapper::mapToResponse)
                .collect(Collectors.toList());
    }

}
