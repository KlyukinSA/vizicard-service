package vizicard.repository;

import com.amazonaws.services.ssmincidents.model.IncidentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.dto.PublicationDTO;
import vizicard.model.Profile;
import vizicard.model.Publication;

import java.util.Arrays;
import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Integer> {
    List<Publication> findAllByOwner(Profile owner);

    List<Publication> findAllByOwnerAndProfile(Profile owner, Profile profile);
}
