package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.*;

import java.util.List;

public interface RelationRepository extends JpaRepository<Relation, Integer> {

    Relation findByAccountOwnerAndCard(Account account, Card card);

    Relation findByTypeAndCard(RelationType relationType, Card card);

    List<Relation> findAllByAccountOwnerAndCardTypeTypeAndType(Account owner, CardTypeEnum cardTypeEnum, RelationType relationType);

    List<Relation> findAllByCardAndAccountOwnerType(Card card, AccountType accountType);

    List<Relation> findAllByTypeAndAccountOwner(RelationType relationType, Account owner);

    Relation findByCardOwnerAndCardTypeTypeAndType(Card card, CardTypeEnum cardTypeEnum, RelationType relationType);

    List<Relation> findAllByCardAndType(Card group, RelationType relationType);

    Relation findByCardOwnerAndCard(Card member, Card group);

    List<Relation> findAllByCardAndTypeAndGroupStatus(Card group, RelationType relationType, GroupMemberStatus status);
}
