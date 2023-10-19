package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.model.Publication;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Integer> {
    List<Publication> findAllByOwner(Account owner);

    List<Publication> findAllByCard(Card page);
}
