package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.ContactEnum;
import vizicard.model.ContactType;

import java.util.List;

public interface ContactTypeRepository extends JpaRepository<ContactType, Integer> {
    ContactType findByType(ContactEnum type);

    ContactType findByUrlBase(String urlBase);
}
