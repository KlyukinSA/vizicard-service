package vizicard.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Card;
import vizicard.model.detail.Education;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Integer> {
    Education findByCardOwnerAndIndividualId(Card user, Integer id);

    List<Education> findAllByCardOwner(Card card);
}
