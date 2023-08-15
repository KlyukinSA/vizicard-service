package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Action;

public interface ActionRepository extends JpaRepository<Action, Integer> {
}
