package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import vizicard.dto.profile.ProfileCreateDTO;
import vizicard.dto.SigninDTO;
import vizicard.dto.AuthResponseDTO;
import vizicard.dto.UserSignupDTO;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.ProfileRepository;
import vizicard.repository.ShortnameRepository;
import vizicard.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
    private final ShortnameService shortnameService;

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private final ProfileService profileService;

    public AuthResponseDTO signin(SigninDTO dto) {
        try {
            Profile profile = profileRepository.findByUsername(dto.getUsername());
            String id = String.valueOf(profile.getId());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, dto.getPassword()));
            return new AuthResponseDTO(jwtTokenProvider.createToken(id), shortnameService.getMainShortname(profile));
        } catch (Exception e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public AuthResponseDTO signup(UserSignupDTO dto) {
        if (!profileRepository.existsByUsername(dto.getUsername())) {
            ProfileCreateDTO dto1 = new ProfileCreateDTO();
            dto1.setName(dto.getName());
            dto1.setType(ProfileType.USER);

            Profile profile = profileService.createProfile(dto1, null, dto.getUsername(), dto.getPassword());

            return new AuthResponseDTO(jwtTokenProvider.createToken(String.valueOf(profile.getId())), shortnameService.getMainShortname(profile));
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
