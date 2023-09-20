package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vizicard.dto.ProfileResponseDTO;
import vizicard.dto.profile.WorkerCreateDTO;
import vizicard.model.Profile;
import vizicard.service.CompanyService;
import vizicard.service.ProfileService;
import vizicard.utils.ProfileMapper;

@RestController
@RequestMapping("/profiles/my-company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final ProfileMapper profileMapper;

    @PostMapping("workers")
    ProfileResponseDTO createWorker(WorkerCreateDTO dto) {
        return profileMapper.mapToResponse(companyService.createWorker(dto));
    }

}
