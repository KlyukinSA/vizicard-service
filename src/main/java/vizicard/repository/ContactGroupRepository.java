package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.dto.contact.ContactGroupResponse;
import vizicard.model.ContactGroup;

import java.util.List;

public interface ContactGroupRepository extends JpaRepository<ContactGroup, Integer> {
    List<ContactGroup> findAllByWritingLike(String writing);
}
