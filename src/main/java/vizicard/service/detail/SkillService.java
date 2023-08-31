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

    public void addSkills(List<String> dto) {
        Profile user = profileProvider.getUserFromAuth();
        for (String s : dto) {
            Skill detail = new Skill(user, s);
            try {
                repository.save(detail);
            } catch (Exception ignored) {}
        }
    }

    public void deleteSkills(List<Integer> ids) {
        Profile user = profileProvider.getUserFromAuth();
        for (Integer id : ids) {
            Skill detail = repository.findById(id).get();
            if (Objects.equals(detail.getOwner().getId(), user.getId())) {
                detail.setStatus(false);
                repository.save(detail);
            }
        }
    }

}
