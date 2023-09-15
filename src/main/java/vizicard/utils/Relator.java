package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vizicard.model.Profile;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;

@Component
@RequiredArgsConstructor
public class Relator {

    private final RelationRepository relationRepository;

    public Relation relate(Profile owner, Profile profile, RelationType relationType) {
        Relation relation = relationRepository.findByOwnerAndProfile(owner, profile);
        if (relation == null) {
            return relationRepository.save(new Relation(owner, profile, relationType));
        } else {
            if (!relation.isStatus()) {
                relation.setStatus(true);
            }
            relation.setType(relationType);
            return relationRepository.save(relation);
        }
    }
}
