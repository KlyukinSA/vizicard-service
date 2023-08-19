package vizicard.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.dto.detail.EducationDTO;
import vizicard.model.Action;
import vizicard.model.Profile;
import vizicard.model.detail.Education;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Integer> {
    List<Education> findAllByOwner(Profile user);
}
