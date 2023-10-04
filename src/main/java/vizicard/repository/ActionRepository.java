package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Action;
import vizicard.model.ActionType;
import vizicard.model.Profile;

import java.util.Date;
import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Integer> {
    Integer countByProfileAndCreateAtBetweenAndType(Profile target, Date start, Date stop, ActionType actionType);

    List<Action> findAllByProfileAndTypeAndCreateAtBetween(Profile profile, ActionType actionType, Date start, Date stop);

    List<Action> findAllByProfileAndType(Profile profile, ActionType actionType);

//	@Query("SELECT a.type AS type, COUNT(a.type) AS count "
//			+ "FROM Action AS a GROUP BY a.type")
//	List<IActionCount> countActionStats(Profile profile);
}
