package vizicard.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.profile.request.SigninDTO;
import vizicard.dto.profile.response.AuthResponseDTO;
import vizicard.dto.profile.request.SignupDTO;
import vizicard.dto.profile.response.BriefCardResponse;
import vizicard.exception.CustomException;
import vizicard.mapper.CardMapper;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.security.JwtTokenProvider;
import vizicard.service.AuthService;
import vizicard.service.ShortnameService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ShortnameService shortnameService;

    private final ModelMapper modelMapper;
    private final CardMapper cardMapper;

    @Value("${google-auth.client-id}")
    private String clientId;

    @PostMapping("/signin")
    public AuthResponseDTO login(@RequestBody SigninDTO dto) {
        Account account = authService.signin(dto.getUsername(), dto.getPassword());
		return getResponse(account);
    }

    @PostMapping("/signup")
    public AuthResponseDTO register(@RequestBody SignupDTO dto, @RequestParam(required = false) String shortname, @RequestParam(required = false) Integer referrerId) {
        Account account = modelMapper.map(dto, Account.class);
        Card card = modelMapper.map(dto, Card.class);

        authService.signup(account, card, shortname, referrerId);

        return getResponse(account);
    }

    @PostMapping("change-card")
    @PreAuthorize("hasAuthority('PRO')")
    public AuthResponseDTO signInSecondaryCard(Integer id) {
        return getResponse(authService.changeCard(id));
    }

    private AuthResponseDTO getResponse(Account account) {
        return new AuthResponseDTO(
                jwtTokenProvider.createToken(account),
                shortnameService.getMainShortname(account.getCurrentCard())
        );
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
            return getResponse(authService.signinOrSignupWithGoogle(googleIdToken.getPayload(), shortname, referrerId));
        }
    }

    @DeleteMapping
    public void deleteMe() {
        authService.deleteMe();
    }

    @PostMapping("currents")
    @PreAuthorize("isAuthenticated()")
    public List<BriefCardResponse> getCurrentsByAccountIds(@RequestBody List<Integer> ids) {
        return ids.stream()
                .map(authService::getCurrentCardById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(cardMapper::mapToBrief)
                .collect(Collectors.toList());
    }
}
