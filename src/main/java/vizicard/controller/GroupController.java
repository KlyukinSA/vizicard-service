package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.BriefProfileResponseDTO;
import vizicard.service.ProfileService;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class GroupController {

    private final ProfileService profileService;

    @GetMapping("/{id}/members")
    @PreAuthorize("isAuthenticated()")
    public List<BriefProfileResponseDTO> getAllGroupMembers(@PathVariable("id") Integer groupId) {
        return profileService.getAllGroupMembers(groupId);
    }

    @PostMapping("{id}/members")
    @PreAuthorize("isAuthenticated()")
    public void addGroupMembers(@PathVariable("id") Integer groupId, @RequestBody List<Integer> memberIds) {
        profileService.addGroupMembers(groupId, memberIds);
    }

    @GetMapping("/me/groups")
    @PreAuthorize("isAuthenticated()")
    public List<BriefProfileResponseDTO> getAllMyGroups() {
        return profileService.getAllMyGroups();
    }

}
