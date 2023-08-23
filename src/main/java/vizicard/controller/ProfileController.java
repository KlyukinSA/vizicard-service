package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.ProfileResponseDTO;
import vizicard.dto.ProfileUpdateDTO;
import vizicard.service.UserService;

@RestController
@RequestMapping("profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ProfileResponseDTO createProfile(@RequestBody ProfileUpdateDTO dto) {
        return userService.createProfile(dto);
    }

}
