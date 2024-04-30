package vizicard.service.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vizicard.dto.detail.ExperienceDTO;
import vizicard.model.Card;
import vizicard.model.CardAttribute;
import vizicard.model.detail.Experience;
import vizicard.repository.detail.ExperienceRepository;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository repository;

    private final ModelMapper modelMapper;

    public Experience createExperience(Card card, ExperienceDTO dto) {
        Experience detail = new Experience(card);
        modelMapper.map(dto, detail);
        detail.setIndividualId(getNextIndividualId(card));
        repository.save(detail);
        return detail;
    }

    public Experience updateExperience(Card card, ExperienceDTO dto, Integer id) {
        Experience detail = repository.findByCardOwnerAndIndividualId(card, id);
        modelMapper.map(dto, detail);
        repository.save(detail);
        return detail;
    }

    private Integer getNextIndividualId(Card card) {
        return repository.findAllByCardOwner(card).stream()
                .mapToInt(CardAttribute::getIndividualId)
                .max().orElse(0) + 1;
    }

    public void deleteExperience(Card card, Integer id) {
        Experience detail = repository.findByCardOwnerAndIndividualId(card, id);
        detail.setStatus(false);
        repository.save(detail);
    }

    public Stream<Experience> getAllOfCard(Card card) {
        return card.getDetailStruct().getExperience().stream()
                .filter(Experience::isStatus);
    }

    public Experience findById(Card card, Integer id) {
        return repository.findByCardOwnerAndIndividualId(card, id);
    }

}
