package vizicard.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.detail.Education;

public interface EducationRepository extends JpaRepository<Education, Integer> {
}
