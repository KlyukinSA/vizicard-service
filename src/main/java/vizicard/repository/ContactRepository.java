package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Contact;
import vizicard.model.Card;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Contact findByOwnerAndOrder(Card owner, Integer order);

    List<Contact> findAllByOwner(Card card);
}
