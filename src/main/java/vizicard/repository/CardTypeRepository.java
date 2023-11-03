package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.CardType;
import vizicard.model.CardTypeEnum;

public interface CardTypeRepository extends JpaRepository<CardType, Integer> {
    CardType findByType(CardTypeEnum cardTypeEnum);
}
