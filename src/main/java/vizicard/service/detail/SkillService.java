package vizicard.service.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.dto.detail.SkillDTO;
import vizicard.model.Card;
import vizicard.model.CardAttribute;
import vizicard.model.detail.Skill;
import vizicard.repository.detail.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository repository;

    public List<Skill> changeSkills(Card card, SkillDTO dto) {
        if (dto.getAdd() != null) {
            for (String s : dto.getAdd()) {
                create(card, s);
            }
        }
        if (dto.getDelete() != null) {
            for (Integer id : dto.getDelete()) {
                delete(card, id);
            }
        }
        return repository.findAllByCardOwner(card).stream()
                .filter(Skill::isStatus)
                .collect(Collectors.toList());
    }

    private Integer getNextIndividualId(Card card) {
        return repository.findAllByCardOwner(card).stream()
                .mapToInt(CardAttribute::getIndividualId)
                .max().orElse(0) + 1;
    }

    public Stream<Skill> getAllOfCard(Card card) {
        return card.getDetailStruct().getSkills().stream()
                .filter(Skill::isStatus);
    }

    public Skill create(Card card, String s) {
        Skill skill = repository.findBySkillAndCardOwner(s, card);
        if (skill != null) {
            skill.setStatus(true);
        } else {
            skill = new Skill(card, s);
            skill.setIndividualId(getNextIndividualId(card));
        }
        return repository.save(skill);
    }

    public void delete(Card card, Integer id) {
        Skill detail = repository.findByCardOwnerAndIndividualId(card, id);
        detail.setStatus(false);
        repository.save(detail);
    }

    public Skill findById(Card card, Integer id) {
        return repository.findByCardOwnerAndIndividualId(card, id);
    }

}
