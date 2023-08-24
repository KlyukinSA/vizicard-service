package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import vizicard.service.ProfileService;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;

  @GetMapping("/{id}")
  @ApiOperation(value = "${UserController.search}", response = ProfileResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Something went wrong"), //
      @ApiResponse(code = 403, message = "Access denied"), //
      @ApiResponse(code = 404, message = "The user doesn't exist"), //
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public ProfileResponseDTO search(@ApiParam("Username") @PathVariable Integer id) {
    return profileService.search(id);
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

  @PostMapping("/me/relations")
  public ResponseEntity<?> relate(@RequestParam Integer id) throws Exception {
    return profileService.relate(id);
  }

  @DeleteMapping("/me/relations/{id}")
  @PreAuthorize("isAuthenticated()")
  public void unrelate(@PathVariable("id") Integer id) {
    profileService.unrelate(id);
  }

  @GetMapping("/me/relations")
  @PreAuthorize("isAuthenticated()")
  public List<RelationResponseDTO> getRelations() {
    return profileService.getRelations();
  }

  @PostMapping("/{id}/relations")
  public void leadGenerate(@PathVariable("id") Integer id, @RequestBody LeadGenerationDTO dto) {
    profileService.leadGenerate(id, dto);
  }

  @PostMapping("/{id}/clicks")
  void addClickAction(@PathVariable("id") Integer id) {
    profileService.addClickAction(id);
  }

  @GetMapping("/me/actions")
  @PreAuthorize("isAuthenticated()")
  PageActionDTO getPageStats() {
    return profileService.getPageStats();
  }

  @PutMapping("/me/online")
  @PreAuthorize("isAuthenticated()")
  public ProfileResponseDTO updateMyLastVizit() {
    return profileService.updateMyLastVizit();
  }


  @DeleteMapping("{id}")
  @PreAuthorize("isAuthenticated()")
  public void deleteProfile(@PathVariable("id") Integer id) {
    profileService.deleteProfile(id);
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ProfileResponseDTO createProfile(@RequestBody ProfileCreateDTO dto) {
    return profileService.createProfile(dto);
  }

}