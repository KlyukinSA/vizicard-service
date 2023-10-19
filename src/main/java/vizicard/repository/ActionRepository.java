package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vizicard.model.Account;
import vizicard.model.Action;
import vizicard.model.ActionType;
import vizicard.model.Card;

import java.util.Date;
import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Integer> {
    Integer countByCardAndCreateAtBetweenAndType(Card target, Date start, Date stop, ActionType actionType);

    List<Action> findAllByCardAndTypeAndCreateAtBetween(Card card, ActionType actionType, Date start, Date stop);

    List<Action> findAllByCardAndType(Card card, ActionType actionType);

//    @Query("SELECT COUNT(a.owner) FROM Action AS a WHERE a.profile = ?1 AND a.type = ?2 GROUP BY a.owner")
//    int countByProfileAndTypeDistinctByOwner(Card user, ActionType actionType);

	List<Action> findAllByShortnameReferrerAndType(Card referrer, ActionType actionType);

    List<Action> findAllByOwnerAndTypeOrderByCreateAtDesc(Account owner, ActionType actionType);

//    boolean existsByOwnerAndProfileAndIp(Profile owner, Profile profile, String ip);

//	@Query("SELECT a.type AS type, COUNT(a.type) AS count "
//			+ "FROM Action AS a GROUP BY a.type")
//	List<IActionCount> countActionStats(Profile profile);
}
