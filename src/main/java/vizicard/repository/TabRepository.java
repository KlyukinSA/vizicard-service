package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Card;
import vizicard.model.Tab;
import vizicard.model.TabTypeEnum;

import java.util.List;
import java.util.Optional;

public interface TabRepository extends JpaRepository<Tab, Integer> {
    Tab findByCardOwnerAndOrder(Card owner, Integer order);

    List<Tab> findAllByCardOwner(Card owner);

    Optional<Tab> findByTypeTypeAndCardOwner(TabTypeEnum tabTypeEnum, Card owner);
}
