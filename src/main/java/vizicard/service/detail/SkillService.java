package vizicard.service.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.dto.detail.SkillDTO;
import vizicard.model.Card;
import vizicard.model.detail.Skill;
import vizicard.repository.detail.SkillRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository repository;

    private final ProfileProvider profileProvider;

    public List<Skill> changeSkills(SkillDTO dto) {
        Card user = profileProvider.getUserFromAuth().getCurrentCard();
        if (dto.getAdd() != null) {
            for (String s : dto.getAdd()) {
                Skill skill = repository.findBySkillAndOwner(s, user);
                if (skill != null) {
                    skill.setStatus(true);
                } else {
                    skill = new Skill(user, s);
                }
                repository.save(skill);
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
        return repository.findAllByOwner(user).stream()
                .filter(Skill::isStatus)
                .collect(Collectors.toList());
    }

}
