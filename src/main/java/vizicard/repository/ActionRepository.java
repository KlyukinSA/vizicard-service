package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Action;
import vizicard.model.ActionType;
import vizicard.model.Profile;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Integer> {
    Integer countByPageAndCreateAtBetweenAndType(Profile target, Date start, Date stop, ActionType actionType);
}
