package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.ProfileResponseDTO;
import vizicard.dto.ProfileUpdateDTO;
import vizicard.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("company")
@RequiredArgsConstructor
public class CompanyController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ProfileResponseDTO createCompany(@RequestBody ProfileUpdateDTO dto) {
        return userService.createCompany(dto);
    }

}
