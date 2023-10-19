package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.model.Shortname;
import vizicard.model.ShortnameType;

public interface ShortnameRepository extends JpaRepository<Shortname, Integer> {
    Shortname findByShortname(String sn);

    Shortname findByCardAndType(Card card, ShortnameType type);

    Shortname findByCard(Card card);
}
