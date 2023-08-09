package vizicard.controller;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;

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

  @GetMapping(value = "/{id}")
  @ApiOperation(value = "${UserController.search}", response = UserResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
  @ApiResponses(value = {//
      @ApiResponse(code = 400, message = "Something went wrong"), //
      @ApiResponse(code = 403, message = "Access denied"), //
      @ApiResponse(code = 404, message = "The user doesn't exist"), //
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
  public UserResponseDTO search(@ApiParam("Username") @PathVariable Integer id) {
    return userService.search(id);
  }

  @GetMapping(value = "/me")
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
    return userService.update(dto);
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

  @GetMapping(value = "/{id}/vcard")
  public ResponseEntity<?> vcard(@ApiParam("Username") @PathVariable Integer id) throws IOException {
    byte[] bytes = userService.getVcardBytes(id);
    UserResponseDTO userResponseDTO = userService.search(id); // TODO

    return ResponseEntity.ok()
            .contentType(MediaType.valueOf("text/vcard"))
            .header("Content-Disposition", "attachment; filename=\"" + userResponseDTO.getName() + ".pdf\"")
            .contentLength(bytes.length)
            .body(new InputStreamResource(new ByteArrayInputStream(bytes)));
  }

  @PostMapping("/me/devices")
  @PreAuthorize("isAuthenticated()")
  public boolean addDevice(String word) throws IOException {
    return userService.addDevice(word);
  }

}
