package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import vizicard.dto.detail.EducationDTO;
import vizicard.service.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{id}")
  @ApiOperation(value = "${UserController.search}", response = ProfileResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Something went wrong"), //
      @ApiResponse(code = 403, message = "Access denied"), //
      @ApiResponse(code = 404, message = "The user doesn't exist"), //
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public ProfileResponseDTO search(@ApiParam("Username") @PathVariable Integer id) {
    return userService.search(id);
  }

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "${UserController.me}", response = ProfileResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Something went wrong"), //
      @ApiResponse(code = 403, message = "Access denied"), //
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public ProfileResponseDTO whoami() {
    return userService.whoami();
  }

  @PutMapping("{id}")
  @PreAuthorize("isAuthenticated()")
  public ProfileResponseDTO update(@PathVariable Integer id, @RequestBody ProfileUpdateDTO dto) {
    return userService.update(id, dto);
  }

  @PostMapping("/me/relations")
  public ResponseEntity<?> relate(@RequestParam Integer id) throws Exception {
    return userService.relate(id);
  }

  @DeleteMapping("/me/relations/{id}")
  @PreAuthorize("isAuthenticated()")
  public void unrelate(@PathVariable("id") Integer id) {
    userService.unrelate(id);
  }

  @GetMapping("/me/relations")
  @PreAuthorize("isAuthenticated()")
  public List<RelationResponseDTO> getRelations() {
    return userService.getRelations();
  }

  @PostMapping("/{id}/relations")
  public void leadGenerate(@PathVariable("id") Integer id, @RequestBody LeadGenerationDTO dto) {
    userService.leadGenerate(id, dto);
  }

  @PostMapping("/{id}/clicks")
  void addClickAction(@PathVariable("id") Integer id) {
    userService.addClickAction(id);
  }

  @GetMapping("/me/actions")
  @PreAuthorize("isAuthenticated()")
  PageActionDTO getPageStats() {
    return userService.getPageStats();
  }

  @PutMapping("/me/online")
  @PreAuthorize("isAuthenticated()")
  public ProfileResponseDTO updateMyLastVizit() {
    return userService.updateMyLastVizit();
  }


  @DeleteMapping("{id}")
  @PreAuthorize("isAuthenticated()")
  public void deleteProfile(@PathVariable("id") Integer id) {
    userService.deleteProfile(id);
  }

}
