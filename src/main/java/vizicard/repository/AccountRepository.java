package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vizicard.model.Account;
import vizicard.model.Card;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	Account findByUsername(String username);

    List<Account> findAllByEmployer(Card company);
}
