package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.CustomAttribute;
import vizicard.model.Relation;

public interface CustomAttributeRepository extends JpaRepository<CustomAttribute, Integer> {
    CustomAttribute findByRelationAndName(Relation relation, String name);
}
