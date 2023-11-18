package vizicard.service.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vizicard.dto.detail.ExperienceDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.model.Card;
import vizicard.model.detail.Experience;
import vizicard.repository.detail.ExperienceRepository;
import vizicard.utils.ProfileProvider;

import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository repository;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;

    public ExperienceResponseDTO createExperience(ExperienceDTO dto) {
        Card user = profileProvider.getUserFromAuth().getCurrentCard();
        Experience detail = new Experience(user);
        modelMapper.map(dto, detail);
        repository.save(detail);
        return modelMapper.map(detail, ExperienceResponseDTO.class);
    }

    public ExperienceResponseDTO updateExperience(ExperienceDTO dto, Integer id) {
        Card user = profileProvider.getUserFromAuth().getCurrentCard();
        Experience detail = repository.findById(id).get();
        if (Objects.equals(detail.getCardOwner().getId(), user.getId())) {
            modelMapper.map(dto, detail);
            repository.save(detail);
        }
        return modelMapper.map(detail, ExperienceResponseDTO.class);
    }

    public void deleteExperience(Integer id) {
        Card user = profileProvider.getUserFromAuth().getCurrentCard();
        Experience detail = repository.findById(id).get();
        if (Objects.equals(detail.getCardOwner().getId(), user.getId())) {
            detail.setStatus(false);
            repository.save(detail);
        }
    }

    public Stream<Experience> getOfCurrentCard() {
        return profileProvider.getUserFromAuth().getCurrentCard().getDetailStruct().getExperience().stream()
                .filter(Experience::isStatus);
    }
}
