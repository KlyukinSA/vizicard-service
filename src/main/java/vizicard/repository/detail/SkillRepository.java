package vizicard.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.detail.Skill;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
}
