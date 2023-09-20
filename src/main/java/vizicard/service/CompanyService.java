package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.dto.ProfileResponseDTO;
import vizicard.dto.profile.ProfileCreateDTO;
import vizicard.dto.profile.WorkerCreateDTO;
import vizicard.exception.CustomException;
import vizicard.model.Profile;
import vizicard.model.ProfileType;
import vizicard.model.RelationType;
import vizicard.repository.ProfileRepository;
import vizicard.repository.RelationRepository;
import vizicard.utils.ProfileMapper;
import vizicard.utils.ProfileProvider;
import vizicard.utils.RelationValidator;
import vizicard.utils.Relator;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final ProfileService profileService;
    private final ProfileProvider profileProvider;
    private final ProfileRepository profileRepository;

    private final ModelMapper modelMapper;
    private final RelationValidator relationValidator;
    private final Relator relator;

    public Profile createWorker(WorkerCreateDTO dto) {
        Profile user = profileProvider.getUserFromAuth();
        Profile company = user.getCompany();
        if (company == null) {
            throw new CustomException("you dont have a company", HttpStatus.BAD_REQUEST);
        }
        ProfileCreateDTO dto1 = modelMapper.map(dto, ProfileCreateDTO.class);
        dto1.setType(ProfileType.WORKER);
        Profile worker = profileService.createProfile(dto1, user, dto.getUsername(), dto.getPassword());
        addToCompany(worker, company);
        return profileRepository.save(worker);
    }

    private void addToCompany(Profile profile, Profile company) { // TODO use public? (in updateProfile())
        relator.relate(profile, company, RelationType.USUAL);
        profile.setCompany(company);
    }

}
