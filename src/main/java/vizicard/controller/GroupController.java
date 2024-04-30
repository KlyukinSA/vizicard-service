package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.GroupMemberStatusDTO;
import vizicard.dto.GroupMemberStatusListResponseDTO;
import vizicard.dto.profile.response.BriefCardResponse;
import vizicard.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final ModelMapper modelMapper;

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

    @PostMapping("{id}/statuses")
    @PreAuthorize("isAuthenticated()")
    public GroupMemberStatusDTO createStatus(@PathVariable("id") Integer groupId, @RequestBody String name) {
        return modelMapper.map(groupService.createStatus(groupId, name), GroupMemberStatusDTO.class);
    }

    @PutMapping("{id}/members/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public GroupMemberStatusDTO changeMemberStatus(@PathVariable("id") Integer groupId, @PathVariable Integer memberId, @RequestBody Integer statusId) {
        return modelMapper.map(groupService.changeMemberStatus(groupId, memberId, statusId), GroupMemberStatusDTO.class);
    }

    @GetMapping("{id}/members-by-statuses")
    @PreAuthorize("isAuthenticated()")
    public List<GroupMemberStatusListResponseDTO> getAllStatusesWithTheirMembers(@PathVariable("id") Integer groupId) {
        return groupService.getAllStatusesWithTheirMembers(groupId);
    }

}
