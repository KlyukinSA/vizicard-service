package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vizicard.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	Account findByUsername(String username);
}
