package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.*;

import java.util.List;

public interface RelationRepository extends JpaRepository<Relation, Integer> {

    Relation findByAccountOwnerAndCard(Account account, Card card);

    List<Relation> findAllByCard(Card card);

    Relation findByTypeAndCard(RelationType relationType, Card card);

    List<Relation> findAllByAccountOwnerAndCardType(Account owner, CardType cardType);

    List<Relation> findAllByCardAndAccountOwnerType(Card card, AccountType accountType);

    List<Relation> findAllByTypeAndAccountOwner(RelationType relationType, Account owner);

    Relation findByCardOwnerAndCardTypeAndType(Card card, CardType cardType, RelationType relationType);
}
