package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.profile.response.BriefCardResponse;
import vizicard.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/{id}/members")
    @PreAuthorize("isAuthenticated()")
    public List<BriefCardResponse> getAllGroupMembers(@PathVariable("id") Integer groupId) {
        return groupService.getAllGroupMembers(groupId);
    }

    @PostMapping("{id}/members")
    @PreAuthorize("isAuthenticated()")
    public List<BriefCardResponse> addGroupMembers(@PathVariable("id") Integer groupId, @RequestBody List<Integer> memberIds) {
        return groupService.addGroupMembers(groupId, memberIds);
    }

    @GetMapping("/me/groups")
    @PreAuthorize("isAuthenticated()")
    public List<BriefCardResponse> getAllMyGroups() {
        return groupService.getAllMyGroups();
    }

}
