package vizicard.controller.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.detail.*;
import vizicard.model.detail.Education;
import vizicard.service.detail.EducationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("profiles/me/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public EducationResponseDTO createEducation(@RequestBody EducationDTO dto) {
        return mapToResponse(educationService.createEducation(dto));
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public EducationResponseDTO updateEducation(@RequestBody EducationDTO dto, @PathVariable("id") Integer id) {
        return mapToResponse(educationService.updateEducation(dto, id));
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
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private EducationResponseDTO mapToResponse(Education detail) {
        EducationResponseDTO res = modelMapper.map(detail, EducationResponseDTO.class);
        res.setId(detail.getIndividualId());
        return res;
    }

}
