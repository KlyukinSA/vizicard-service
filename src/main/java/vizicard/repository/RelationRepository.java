package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.*;

import java.util.List;

public interface RelationRepository extends JpaRepository<Relation, Integer> {

    Relation findByOwnerAndCard(Account account, Card card);

    List<Relation> findAllByCard(Card card);

    Relation findByTypeAndCard(RelationType relationType, Card card);

    List<Relation> findAllByOwnerAndCardType(Account owner, ProfileType profileType);

    List<Relation> findAllByCardAndOwnerMainCardType(Card card, ProfileType profileType);

    List<Relation> findAllByTypeAndOwner(RelationType relationType, Account owner);

}
