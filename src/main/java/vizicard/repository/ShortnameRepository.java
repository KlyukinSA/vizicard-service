package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Profile;
import vizicard.model.Shortname;
import vizicard.model.ShortnameType;

public interface ShortnameRepository extends JpaRepository<Shortname, Integer> {
    Shortname findByShortname(String word);

    Shortname findByOwnerAndType(Profile user, ShortnameType type);
}
