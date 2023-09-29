package vizicard.service;

import lombok.RequiredArgsConstructor;
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
import vizicard.repository.ShortnameRepository;
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
    private final ShortnameRepository shortnameRepository;

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

    public AuthResponseDTO signup(SignupDTO dto, String shortname, Integer referrerId) {
        if (!profileRepository.existsByUsername(dto.getUsername())) {
            ProfileCreateDTO dto1 = new ProfileCreateDTO();
            dto1.setName(dto.getName());
            dto1.setType(ProfileType.USER);
            Profile profile = profileService.createProfile(dto1, null, dto.getUsername(), dto.getPassword());
            refer(profile, shortname, referrerId);
            return getResponse(profile);
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private void refer(Profile profile, String sn, Integer referrerId) {
        Profile referrer;
        if (referrerId != null) {
            referrer = profileProvider.getTarget(referrerId);
        } else if (sn != null) {
            Shortname shortname = shortnameRepository.findByShortname(sn);
            if (shortname == null) {
                return;
            }
            if (shortname.getOwner() != null) {
                referrer = shortname.getOwner();
            } else if (shortname.getReferrer() != null) {
                referrer = shortname.getReferrer();
                if (shortname.getType() == ShortnameType.DEVICE) {
                    shortname.setOwner(profile);
                    shortnameRepository.save(shortname);
                }
            } else {
                return;
            }
        } else {
            return;
        }
        relationRepository.save(new Relation(referrer, profile, RelationType.CREATED_REFERRAL));
        relationRepository.save(new Relation(profile, referrer, RelationType.FOLLOWED_REFERRAL));
    }

    AuthResponseDTO getResponse(Profile profile) {
        return new AuthResponseDTO(
                jwtTokenProvider.createToken(profile),
                shortnameService.getMainShortname(profile)
        );
    }

}
