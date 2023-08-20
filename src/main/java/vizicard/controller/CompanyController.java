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

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ProfileResponseDTO updateMyCompany(@RequestBody ProfileUpdateDTO dto) {
        return userService.updateMyCompany(dto);
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public void deleteMyCompany() {
        userService.deleteMyCompany();
    }

    @PostMapping("avatar")
    @PreAuthorize("isAuthenticated()")
    public ProfileResponseDTO updateMyCompanyAvatar(@RequestPart("file") MultipartFile file) throws IOException {
        return userService.updateMyCompanyAvatar(file);
    }

}
