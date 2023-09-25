package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.BriefProfileResponseDTO;
import vizicard.dto.ProfileResponseDTO;
import vizicard.dto.profile.WorkerCreateDTO;
import vizicard.model.Profile;
import vizicard.service.CompanyService;
import vizicard.service.ProfileService;
import vizicard.utils.ProfileMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profiles/my-company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final ProfileMapper profileMapper;

    @PostMapping("workers")
    @PreAuthorize("hasAuthority('PRO')")
    ProfileResponseDTO createWorker(@RequestBody WorkerCreateDTO dto) {
        return profileMapper.mapToResponse(companyService.createWorker(dto));
    }

    @GetMapping("workers")
    List<BriefProfileResponseDTO> getAllWorkers() {
        return companyService.getAllWorkers().stream()
                .map(profileMapper::mapToBrief)
                .collect(Collectors.toList());
    }

}
