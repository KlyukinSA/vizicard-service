package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.ContactType;

public interface ContactTypeRepository extends JpaRepository<ContactType, Integer> {
}
