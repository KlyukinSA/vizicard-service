package vizicard.service.detail;

import com.amazonaws.services.greengrassv2.model.LambdaIsolationMode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vizicard.dto.ProfileResponseDTO;
import vizicard.dto.detail.EducationDTO;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.model.Profile;
import vizicard.model.detail.Education;
import vizicard.repository.ProfileRepository;
import vizicard.repository.detail.EducationRepository;
import vizicard.service.UserService;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;

    public EducationResponseDTO createEducation(EducationDTO dto) {
        Profile user = profileProvider.getUserFromAuth();
        Education education = new Education(user);
        modelMapper.map(dto, education);
        educationRepository.save(education);
        return modelMapper.map(education, EducationResponseDTO.class);
    }

    public EducationResponseDTO updateEducation(EducationDTO dto, Integer id) {
        Profile user = profileProvider.getUserFromAuth();
        Education education = educationRepository.findById(id).get();
        if (Objects.equals(education.getOwner().getId(), user.getId())) {
            modelMapper.map(dto, education);
            educationRepository.save(education);
        }
        return modelMapper.map(education, EducationResponseDTO.class);
    }

    public void deleteEducation(Integer id) {
        Profile user = profileProvider.getUserFromAuth();
        Education education = educationRepository.findById(id).get();
        if (Objects.equals(education.getOwner().getId(), user.getId())) {
            education.setStatus(false);
            educationRepository.save(education);
        }
    }

}
