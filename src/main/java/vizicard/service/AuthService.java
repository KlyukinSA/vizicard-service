package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vizicard.dto.ContactRequest;
import vizicard.dto.SigninDTO;
import vizicard.dto.UserSignupDTO;
import vizicard.exception.CustomException;
import vizicard.model.ContactEnum;
import vizicard.model.Profile;
import vizicard.model.ProfileType;
import vizicard.repository.ProfileRepository;
import vizicard.security.JwtTokenProvider;
import vizicard.utils.ContactUpdater;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private final ModelMapper modelMapper;
    private final ContactUpdater contactUpdater;

    public String signin(SigninDTO dto) {
        try {
            Profile profile = profileRepository.findByUsername(dto.getUsername());
            String id = String.valueOf(profile.getId());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, dto.getPassword()));
            return jwtTokenProvider.createToken(id);
        } catch (Exception e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signup(UserSignupDTO dto) {
        Profile profile = modelMapper.map(dto, Profile.class);
        if (!profileRepository.existsByUsername(profile.getUsername())) {
            profile.setPassword(passwordEncoder.encode(profile.getPassword()));
            profile.setProfileType(ProfileType.USER);
            profile = profileRepository.save(profile);
            contactUpdater.updateContact(profile, new ContactRequest(ContactEnum.MAIL, profile.getUsername()));
            String id = String.valueOf(profile.getId());
            return jwtTokenProvider.createToken(id);
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
