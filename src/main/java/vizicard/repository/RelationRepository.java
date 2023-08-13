package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Relation;

public interface RelationRepository extends JpaRepository<Relation, Integer> {
}
