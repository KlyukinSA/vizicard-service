package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Profile;
import vizicard.model.Publication;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Integer> {
    List<Publication> findAllByOwner(Profile owner);

    List<Publication> findAllByOwnerAndProfile(Profile owner, Profile profile);

    List<Publication> findAllByProfile(Profile page);
}
