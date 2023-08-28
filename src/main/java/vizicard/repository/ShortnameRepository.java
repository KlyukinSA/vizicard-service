package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Shortname;

public interface ShortnameRepository extends JpaRepository<Shortname, Integer> {
    Shortname findByShortname(String word);
}
