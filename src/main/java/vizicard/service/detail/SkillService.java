package vizicard.service.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vizicard.dto.detail.ExperienceDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.SkillDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.model.Profile;
import vizicard.model.detail.Experience;
import vizicard.model.detail.Skill;
import vizicard.repository.detail.ExperienceRepository;
import vizicard.repository.detail.SkillRepository;
import vizicard.utils.ProfileProvider;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository repository;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;

    public SkillResponseDTO createSkill(SkillDTO dto) {
        Profile user = profileProvider.getUserFromAuth();
        Skill detail = new Skill(user);
        modelMapper.map(dto, detail);
        repository.save(detail);
        return modelMapper.map(detail, SkillResponseDTO.class);
    }

    public void deleteSkill(Integer id) {
        Profile user = profileProvider.getUserFromAuth();
        Skill detail = repository.findById(id).get();
        if (Objects.equals(detail.getOwner().getId(), user.getId())) {
            detail.setStatus(false);
            repository.save(detail);
        }
    }

}
