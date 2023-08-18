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
import vizicard.service.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
@Api(tags = "users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

  private final UserService userService;

  @PostMapping("/signin")
  @ApiOperation(value = "${UserController.signin}")
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Something went wrong"), //
      @ApiResponse(code = 422, message = "Invalid username/password supplied")})
  public String login(@RequestBody SigninDTO signinDTO) {
    return userService.signin(signinDTO);
  }

  @PostMapping("/signup")
  @ApiOperation(value = "${UserController.signup}")
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Something went wrong"), //
      @ApiResponse(code = 403, message = "Access denied"), //
      @ApiResponse(code = 422, message = "Username is already in use")})
  public String signup(@ApiParam("Signup User") @RequestBody UserSignupDTO user) {
    return userService.signup(user);
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "${UserController.search}", response = UserResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Something went wrong"), //
      @ApiResponse(code = 403, message = "Access denied"), //
      @ApiResponse(code = 404, message = "The user doesn't exist"), //
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public UserResponseDTO search(@ApiParam("Username") @PathVariable Integer id) {
    return userService.search(id);
  }

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "${UserController.me}", response = UserResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Something went wrong"), //
      @ApiResponse(code = 403, message = "Access denied"), //
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public UserResponseDTO whoami() {
    return userService.whoami();
  }

  @PutMapping("/me")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "${UserController.update}", response = UserResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {//
          @ApiResponse(code = 400, message = "Something went wrong"), //
          @ApiResponse(code = 403, message = "Access denied")})
  public UserResponseDTO update(@ApiParam("Update User") @RequestBody UserUpdateDTO dto) {
    return userService.updateMe(dto);
  }

  @PostMapping("/me/avatar")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "${UserController.updateAvatar}", response = UserResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  public UserResponseDTO updateAvatar(@RequestPart("file") MultipartFile file) throws IOException {
    return userService.updateAvatar(file);
  }

  @PostMapping("/me/background")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "${UserController.updateAvatar}", response = UserResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  public UserResponseDTO updateBackground(@RequestPart("file") MultipartFile file) throws IOException {
    return userService.updateBackground(file);
  }

//  @GetMapping(value = "/{id}/vcard")
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

  @PostMapping("/me/devices")
  @PreAuthorize("isAuthenticated()")
  public boolean addDevice(@RequestParam String url) {
    return userService.addDevice(url);
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

  @PutMapping("/me/company")
  @PreAuthorize("isAuthenticated()")
  public UserResponseDTO updateMyCompany(@RequestBody UserUpdateDTO dto) {
    return userService.updateMyCompany(dto);
  }

}
