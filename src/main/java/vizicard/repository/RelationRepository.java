package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.*;

import java.util.List;

public interface RelationRepository extends JpaRepository<Relation, Integer> {

    Relation findByAccountOwnerAndCard(Account account, Card card);

    List<Relation> findAllByCard(Card card);

    Relation findByTypeAndCard(RelationType relationType, Card card);

    List<Relation> findAllByAccountOwnerAndCardType(Account owner, ProfileType profileType);

    List<Relation> findAllByCardAndAccountOwnerMainCardType(Card card, ProfileType profileType);

    List<Relation> findAllByTypeAndAccountOwner(RelationType relationType, Account owner);

}
