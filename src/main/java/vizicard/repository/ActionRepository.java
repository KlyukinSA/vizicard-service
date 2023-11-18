package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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

	List<Action> findAllByShortnameReferrerAndType(Card referrer, ActionType actionType);

    List<Action> findAllByAccountOwnerAndTypeOrderByCreateAtDesc(Account owner, ActionType actionType);
}
