package vizicard.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Card;
import vizicard.model.detail.Experience;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Integer> {
    List<Experience> findAllByCardOwner(Card card);

    Experience findByCardOwnerAndIndividualId(Card owner, Integer individualId);
}
