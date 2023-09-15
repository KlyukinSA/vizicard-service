package vizicard.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vizicard.model.detail.Education;
import vizicard.model.detail.EducationType;

@Repository
public interface EducationTypeRepository extends JpaRepository<EducationType, Integer> {
}
