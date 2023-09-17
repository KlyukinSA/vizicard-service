package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.ContactGroup;

public interface ContactGroupRepository extends JpaRepository<ContactGroup, Integer> {
}
