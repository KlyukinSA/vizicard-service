package vizicard.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Card;
import vizicard.model.detail.Skill;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
	List<Skill> findAllByCardOwner(Card card);

	Skill findByValueAndCardOwner(String s, Card owner);

    Skill findByCardOwnerAndIndividualId(Card user, Integer id);
}
