package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Profile;
import vizicard.model.Relation;

import java.util.Arrays;
import java.util.List;

public interface RelationRepository extends JpaRepository<Relation, Integer> {
    Relation findByOwnerAndProfile(Profile owner, Profile target);

    List<Relation> findAllByOwnerOrderByProfileNameAsc(Profile owner);

    List<Relation> findAllByProfile(Profile target);
}
