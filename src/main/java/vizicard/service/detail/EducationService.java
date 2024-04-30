package vizicard.service.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vizicard.dto.detail.EducationDTO;
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

    public Education createEducation(Card card, EducationDTO dto) {
        Education education = new Education(card);
        modelMapper.map(dto, education);
        education.setId(null);
        education.setType(educationTypeRepository.findById(dto.getTypeId()).get());
        education.setIndividualId(getNextIndividualId(card));
        educationRepository.save(education);
        return education;
    }

    private Integer getNextIndividualId(Card card) {
        return educationRepository.findAllByCardOwner(card).stream()
                .mapToInt(CardAttribute::getIndividualId)
                .max().orElse(0) + 1;
    }

    public Education updateEducation(Card card, EducationDTO dto, Integer id) {
        Education education = educationRepository.findByCardOwnerAndIndividualId(card, id);
        if (Objects.equals(education.getCardOwner().getId(), card.getId())) {
            if (dto.getTypeId() != null) {
                education.setType(educationTypeRepository.findById(dto.getTypeId()).get());
            }
            dto.setTypeId(null);
            modelMapper.map(dto, education);
            educationRepository.save(education);
        }
        return education;
    }

    public void deleteEducation(Card card, Integer id) {
        Education education = educationRepository.findByCardOwnerAndIndividualId(card, id);
        education.setStatus(false);
        educationRepository.save(education);
    }

    public List<EducationTypeDTO> findAllTypes() {
        return educationTypeRepository.findAll().stream()
                .map((val) -> modelMapper.map(val, EducationTypeDTO.class))
                .collect(Collectors.toList());
    }

    public Education findById(Card card, Integer id) {
        return educationRepository.findByCardOwnerAndIndividualId(card, id);
    }

    public Stream<Education> getAllOfCard(Card card) {
        return card.getDetailStruct().getEducation().stream()
                .filter(CardAttribute::isStatus);
    }

}
