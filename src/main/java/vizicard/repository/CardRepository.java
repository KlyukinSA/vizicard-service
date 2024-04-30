package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vizicard.model.Account;
import vizicard.model.Card;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Integer> {
    List<Card> findAllByAccount(Account account);
}
