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

    private final ProfileProvider profileProvider;

    public void changeSkills(SkillDTO dto) {
        Profile user = profileProvider.getUserFromAuth();
        if (dto.getAdd() != null) {
            for (String s : dto.getAdd()) {
                Skill detail = new Skill(user, s);
                try {
                    repository.save(detail);
                } catch (Exception ignored) {
                }
            }
        }
        if (dto.getDelete() != null) {
            for (Integer id : dto.getDelete()) {
                Skill detail = repository.findById(id).get();
                if (Objects.equals(detail.getOwner().getId(), user.getId())) {
                    detail.setStatus(false);
                    repository.save(detail);
                }
            }
        }
    }

}
