package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Profile;
import vizicard.model.Relation;

public interface RelationRepository extends JpaRepository<Relation, Integer> {
    Relation findByOwnerAndProfile(Profile owner, Profile target);
}
