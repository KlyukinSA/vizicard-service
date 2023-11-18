package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Contact;
import vizicard.model.Card;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Contact findByCardOwnerAndOrder(Card owner, Integer order);

    List<Contact> findAllByCardOwner(Card card);
}
