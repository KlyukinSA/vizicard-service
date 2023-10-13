package vizicard.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Profile;
import vizicard.model.detail.Skill;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
	List<Skill> findAllByOwner(Profile profile);

	Skill findBySkillAndOwner(String s, Profile owner);
}
