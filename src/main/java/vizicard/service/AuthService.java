package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import vizicard.dto.profile.ProfileCreateDTO;
import vizicard.dto.SigninDTO;
import vizicard.dto.AuthResponseDTO;
import vizicard.dto.SignupDTO;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.ProfileRepository;
import vizicard.repository.RelationRepository;
import vizicard.security.JwtTokenProvider;
import vizicard.utils.ProfileProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
    private final ShortnameService shortnameService;

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private final ProfileService profileService;
    private final ProfileProvider profileProvider;

    private final RelationRepository relationRepository;

    public AuthResponseDTO signin(SigninDTO dto) {
        try {
            Profile profile = profileRepository.findByUsername(dto.getUsername());
            String id = String.valueOf(profile.getId());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, dto.getPassword()));
            return getResponse(profile);
        } catch (Exception e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public AuthResponseDTO signup(SignupDTO dto, Integer referralId) {
        if (!profileRepository.existsByUsername(dto.getUsername())) {
            ProfileCreateDTO dto1 = new ProfileCreateDTO();
            dto1.setName(dto.getName());
            dto1.setType(ProfileType.USER);
            Profile profile = profileService.createProfile(dto1, null, dto.getUsername(), dto.getPassword());

            if (referralId != null) {
                Profile createdReferral = profileProvider.getTarget(referralId);
                relationRepository.save(new Relation(createdReferral, profile, RelationType.CREATED_REFERRAL));
                relationRepository.save(new Relation(profile, createdReferral, RelationType.FOLLOWED_REFERRAL));
            }

            return getResponse(profile);
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    AuthResponseDTO getResponse(Profile profile) {
        return new AuthResponseDTO(
                jwtTokenProvider.createToken(profile),
                shortnameService.getMainShortname(profile)
        );
    }

}
