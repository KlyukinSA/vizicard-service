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
import vizicard.model.*;
import vizicard.repository.ProfileRepository;
import vizicard.repository.RelationRepository;
import vizicard.security.JwtTokenProvider;
import vizicard.utils.ContactUpdater;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
//    private final ContactUpdater contactUpdater;
    private final RelationRepository relationRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private final ModelMapper modelMapper;

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
            profile = saveNewProfileBasedOn(profile);
//            contactUpdater.updateContact(profile, new ContactRequest(ContactEnum.MAIL, profile.getUsername()));
            relationRepository.save(new Relation(profile, profile, RelationType.OWNER));
            return jwtTokenProvider.createToken(String.valueOf(profile.getId()));
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private Profile saveNewProfileBasedOn(Profile profile) {
        profile.setPassword(passwordEncoder.encode(profile.getPassword()));
        profile.setType(ProfileType.USER);
        return profileRepository.save(profile);
    }

}
