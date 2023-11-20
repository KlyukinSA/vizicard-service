package vizicard.service.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vizicard.dto.detail.EducationDTO;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.EducationTypeDTO;
import vizicard.model.Card;
import vizicard.model.CardAttribute;
import vizicard.model.detail.Education;
import vizicard.repository.detail.EducationRepository;
import vizicard.repository.detail.EducationTypeRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final EducationTypeRepository educationTypeRepository;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;

    public Education createEducation(EducationDTO dto) {
        Card user = profileProvider.getUserFromAuth().getCurrentCard();
        Education education = new Education(user);
        modelMapper.map(dto, education);
        education.setId(null);
        education.setType(educationTypeRepository.findById(dto.getTypeId()).get());
        education.setIndividualId(getNextIndividualId(user));
        educationRepository.save(education);
        return education;
    }

    private Integer getNextIndividualId(Card card) {
        return educationRepository.findAllByCardOwner(card).stream()
                .mapToInt(CardAttribute::getIndividualId)
                .max().orElse(0) + 1;
    }

    public Education updateEducation(EducationDTO dto, Integer id) {
        Card user = profileProvider.getUserFromAuth().getCurrentCard();
        Education education = educationRepository.findByCardOwnerAndIndividualId(user, id);
        if (Objects.equals(education.getCardOwner().getId(), user.getId())) {
            if (dto.getTypeId() != null) {
                education.setType(educationTypeRepository.findById(dto.getTypeId()).get());
            }
            dto.setTypeId(null);
            modelMapper.map(dto, education);
            educationRepository.save(education);
        }
        return education;
    }

    public void deleteEducation(Integer id) {
        Card user = profileProvider.getUserFromAuth().getCurrentCard();
        Education education = educationRepository.findByCardOwnerAndIndividualId(user, id);
        if (Objects.equals(education.getCardOwner().getId(), user.getId())) {
            education.setStatus(false);
            educationRepository.save(education);
        }
    }

    public List<EducationTypeDTO> findAllTypes() {
        return educationTypeRepository.findAll().stream()
                .map((val) -> modelMapper.map(val, EducationTypeDTO.class))
                .collect(Collectors.toList());
    }

    public Stream<Education> getOfCurrentCard() {
        return profileProvider.getUserFromAuth().getCurrentCard().getDetailStruct().getEducation().stream()
                .filter(Education::isStatus);
    }

}
