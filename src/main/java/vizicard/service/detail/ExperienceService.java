package vizicard.service.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vizicard.dto.detail.EducationDTO;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.model.Profile;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;
import vizicard.repository.detail.EducationRepository;
import vizicard.repository.detail.ExperienceRepository;
import vizicard.utils.ProfileProvider;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExperienceService {


    private final ExperienceRepository repository;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;

    public ExperienceResponseDTO createExperience(ExperienceDTO dto) {
        Profile user = profileProvider.getUserFromAuth();
        Experience detail = new Experience(user);
        modelMapper.map(dto, detail);
        repository.save(detail);
        return modelMapper.map(detail, ExperienceResponseDTO.class);
    }

    public ExperienceResponseDTO updateExperience(ExperienceDTO dto, Integer id) {
        Profile user = profileProvider.getUserFromAuth();
        Experience detail = repository.findById(id).get();
        if (Objects.equals(detail.getOwner().getId(), user.getId())) {
            modelMapper.map(dto, detail);
            repository.save(detail);
        }
        return modelMapper.map(detail, ExperienceResponseDTO.class);
    }

    public void deleteExperience(Integer id) {
        Profile user = profileProvider.getUserFromAuth();
        Experience detail = repository.findById(id).get();
        if (Objects.equals(detail.getOwner().getId(), user.getId())) {
            detail.setStatus(false);
            repository.save(detail);
        }
    }

}
