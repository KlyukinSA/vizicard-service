package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import vizicard.dto.profile.ProfileCreateDTO;
import vizicard.dto.profile.ProfileUpdateDTO;
import vizicard.service.ProfileService;
import vizicard.utils.ProfileMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;
  private final ProfileMapper profileMapper;

  @GetMapping("/{shortname}")
  public ProfileResponseDTO searchByShortname(@PathVariable String shortname) {
    return profileService.searchByShortname(shortname);
  }

  @GetMapping("/id{id}")
  public ProfileResponseDTO searchById(@PathVariable Integer id) {
    return profileService.searchById(id);
  }

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "${UserController.me}", response = ProfileResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Something went wrong"), //
      @ApiResponse(code = 403, message = "Access denied"), //
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public ProfileResponseDTO whoami() {
    return profileService.whoami();
  }

  @PutMapping("{id}")
  @PreAuthorize("isAuthenticated()")
  public ProfileResponseDTO update(@PathVariable Integer id, @RequestBody ProfileUpdateDTO dto) {
    return profileService.update(id, dto);
  }

  @DeleteMapping("{id}")
  @PreAuthorize("isAuthenticated()")
  public void deleteProfile(@PathVariable("id") Integer id) {
    profileService.deleteProfile(id);
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ProfileResponseDTO createProfile(@RequestBody ProfileCreateDTO dto) {
    return profileMapper.mapToResponse(profileService.createMyProfile(dto));
  }

  @PostMapping("secondary")
  @PreAuthorize("isAuthenticated()")
  public ProfileResponseDTO createSecondaryProfile(@RequestBody ProfileCreateDTO dto) {
    return profileMapper.mapToResponse(profileService.createSecondaryProfile(dto));
  }

  @GetMapping("accounts")
  public List<BriefProfileResponseDTO> getSecondaryPrimaryAccounts() {
    return profileService.getSecondaryPrimaryAccounts().stream()
            .map(profileMapper::mapToBrief)
            .collect(Collectors.toList());
  }

  @PutMapping("merge")
  public ProfileResponseDTO mergeCustomProfiles(@RequestParam Integer main, @RequestParam Integer secondary) {
    return profileMapper.mapToResponse(profileService.mergeCustomProfiles(main, secondary));
  }

}
