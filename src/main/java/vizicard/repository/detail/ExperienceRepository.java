package vizicard.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;

public interface ExperienceRepository extends JpaRepository<Experience, Integer> {
}
