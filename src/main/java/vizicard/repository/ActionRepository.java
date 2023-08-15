package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Action;
import vizicard.model.ActionType;
import vizicard.model.Profile;

import java.time.Instant;
import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Integer> {
//    List<Action> findAllByPageAndCreateAtBetween(Profile target, Instant start, Instant stop);
    Integer countByPageAndCreateAtBetweenAndType(Profile target, Instant start, Instant stop, ActionType actionType);
}
