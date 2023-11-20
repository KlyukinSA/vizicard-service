package vizicard.service.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.dto.detail.SkillDTO;
import vizicard.model.Card;
import vizicard.model.CardAttribute;
import vizicard.model.detail.Skill;
import vizicard.repository.detail.SkillRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository repository;

    private final ProfileProvider profileProvider;

    public List<Skill> changeSkills(SkillDTO dto) {
        Card user = profileProvider.getUserFromAuth().getCurrentCard();
        if (dto.getAdd() != null) {
            for (String s : dto.getAdd()) {
                Skill skill = repository.findBySkillAndCardOwner(s, user);
                if (skill != null) {
                    skill.setStatus(true);
                } else {
                    skill = new Skill(user, s);
                    skill.setIndividualId(getNextIndividualId(user));
                }
                repository.save(skill);
            }
        }
        if (dto.getDelete() != null) {
            for (Integer id : dto.getDelete()) {
                Skill detail = repository.findByCardOwnerAndIndividualId(user, id);
                if (Objects.equals(detail.getCardOwner().getId(), user.getId())) {
                    detail.setStatus(false);
                    repository.save(detail);
                }
            }
        }
        return repository.findAllByCardOwner(user).stream()
                .filter(Skill::isStatus)
                .collect(Collectors.toList());
    }

    private Integer getNextIndividualId(Card card) {
        return repository.findAllByCardOwner(card).stream()
                .mapToInt(CardAttribute::getIndividualId)
                .max().orElse(0) + 1;
    }

    public Stream<Skill> getOfCurrentCard() {
        return profileProvider.getUserFromAuth().getCurrentCard().getDetailStruct().getSkills().stream()
                .filter(Skill::isStatus);
    }
}
