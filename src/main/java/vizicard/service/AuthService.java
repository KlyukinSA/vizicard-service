package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vizicard.dto.SigninDTO;
import vizicard.dto.AuthResponseDTO;
import vizicard.dto.UserSignupDTO;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.ProfileRepository;
import vizicard.repository.RelationRepository;
import vizicard.repository.ShortnameRepository;
import vizicard.security.JwtTokenProvider;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
    private final RelationRepository relationRepository;
    private final ShortnameRepository shortnameRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private final ModelMapper modelMapper;

    public AuthResponseDTO signin(SigninDTO dto) {
        try {
            Profile profile = profileRepository.findByUsername(dto.getUsername());
            String id = String.valueOf(profile.getId());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, dto.getPassword()));
            return new AuthResponseDTO(jwtTokenProvider.createToken(id), shortnameRepository.findByOwnerAndType(profile, ShortnameType.MAIN).getShortname());
        } catch (Exception e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public AuthResponseDTO signup(UserSignupDTO dto) {
        Profile profile = modelMapper.map(dto, Profile.class);
        if (!profileRepository.existsByUsername(profile.getUsername())) {
            profile.setPassword(passwordEncoder.encode(profile.getPassword()));
            profile.setType(ProfileType.USER);
            profileRepository.save(profile);

            relationRepository.save(new Relation(profile, profile, RelationType.OWNER));
            Shortname shortname = new Shortname(profile, String.valueOf(UUID.randomUUID()), ShortnameType.MAIN);
            shortnameRepository.save(shortname);

            return new AuthResponseDTO(jwtTokenProvider.createToken(String.valueOf(profile.getId())), shortname.getShortname());
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
