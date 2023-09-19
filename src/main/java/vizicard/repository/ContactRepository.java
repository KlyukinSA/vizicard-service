package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Contact;
import vizicard.model.ContactType;
import vizicard.model.Profile;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Contact findByOwnerAndOrder(Profile owner, Integer order);
}
