package vizicard.repository;

import io.swagger.models.auth.In;
import javassist.runtime.Inner;
import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Contact;
import vizicard.model.ContactType;
import vizicard.model.Profile;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Contact findByOwnerAndType(Profile owner, ContactType type);

    Contact[] findByOwner(Profile user);
}
