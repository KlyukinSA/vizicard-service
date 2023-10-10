package vizicard.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vizicard.dto.SigninDTO;
import vizicard.dto.AuthResponseDTO;
import vizicard.dto.SignupDTO;
import vizicard.exception.CustomException;
import vizicard.service.AuthService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @Value("${google-auth.client-id}")
    private String clientId;

    @PostMapping("/signin")
    @ApiOperation(value = "${UserController.signin}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid username/password supplied")})
    public AuthResponseDTO login(@RequestBody SigninDTO signinDTO) {
        return authService.signin(signinDTO);
    }

    @PostMapping("/signup")
    @ApiOperation(value = "${UserController.signup}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 422, message = "Username is already in use")})
    public AuthResponseDTO signup(@RequestBody SignupDTO dto, @RequestParam(required = false) String shortname, @RequestParam(required = false) Integer referrerId) {
        return authService.signup(dto, shortname, referrerId);
    }

    @PostMapping("sign-in-secondary")
    @PreAuthorize("hasAuthority('PRO')")
    public AuthResponseDTO signInSecondary(Integer id) {
        return authService.signInSecondary(id);
    }

    @PostMapping("google")
    public AuthResponseDTO signinOrSignupWithGoogle(@RequestParam String idToken, @RequestParam(required = false) String shortname, @RequestParam(required = false) Integer referrerId) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            throw new CustomException("Invalid ID token", HttpStatus.FORBIDDEN);
        } else {
            return authService.signinOrSignupWithGoogle(googleIdToken.getPayload(), shortname, referrerId);
        }
    }

}
