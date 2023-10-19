package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;

@Component
@RequiredArgsConstructor
public class Relator {

    private final RelationRepository relationRepository;

    public Relation relate(Account owner, Card card, RelationType relationType) {
        Relation relation = relationRepository.findByOwnerAndCard(owner, card);
        if (relation == null) {
            return relationRepository.save(new Relation(owner, card, relationType));
        } else {
            if (!relation.isStatus()) {
                relation.setStatus(true);
            }
            relation.setType(relationType);
            return relationRepository.save(relation);
        }
    }

}
