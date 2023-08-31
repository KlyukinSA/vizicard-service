package vizicard.service.detail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vizicard.dto.detail.SkillDTO;
import vizicard.model.Profile;
import vizicard.model.detail.Skill;
import vizicard.repository.detail.SkillRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository repository;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;

    public void addSkills(List<SkillDTO> dtos) {
        Profile user = profileProvider.getUserFromAuth();
        for (SkillDTO dto : dtos) {
            Skill detail = new Skill(user);
            modelMapper.map(dto, detail);
            try {
                repository.save(detail);
            } catch (Exception ignored) {}
        }
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
