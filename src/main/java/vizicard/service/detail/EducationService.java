package vizicard.service.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vizicard.dto.detail.EducationDTO;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.EducationTypeDTO;
import vizicard.model.Card;
import vizicard.model.detail.Education;
import vizicard.repository.detail.EducationRepository;
import vizicard.repository.detail.EducationTypeRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final EducationTypeRepository educationTypeRepository;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;

    public EducationResponseDTO createEducation(EducationDTO dto) {
        Card user = profileProvider.getUserFromAuth().getCurrentCard();
        Education education = new Education(user);
        modelMapper.map(dto, education);
        educationRepository.save(education);
        return modelMapper.map(education, EducationResponseDTO.class);
    }

    public EducationResponseDTO updateEducation(EducationDTO dto, Integer id) {
        Card user = profileProvider.getUserFromAuth().getCurrentCard();
        Education education = educationRepository.findById(id).get();
        if (Objects.equals(education.getOwner().getId(), user.getId())) {
            education.setType(educationTypeRepository.findById(dto.getTypeId()).get());
            dto.setTypeId(null);
            modelMapper.map(dto, education);
            educationRepository.save(education);
        }
        return modelMapper.map(education, EducationResponseDTO.class);
    }

    public void deleteEducation(Integer id) {
        Card user = profileProvider.getUserFromAuth().getCurrentCard();
        Education education = educationRepository.findById(id).get();
        if (Objects.equals(education.getOwner().getId(), user.getId())) {
            education.setStatus(false);
            educationRepository.save(education);
        }
    }

    public List<EducationTypeDTO> findAllTypes() {
        return educationTypeRepository.findAll().stream()
                .map((val) -> modelMapper.map(val, EducationTypeDTO.class))
                .collect(Collectors.toList());
    }

}
