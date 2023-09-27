package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.model.Profile;
import vizicard.model.Publication;
import vizicard.repository.PublicationRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;

    private final ProfileProvider profileProvider;

    public Publication createPublication(Publication publication, Integer id) {
        publication.setOwner(profileProvider.getUserFromAuth());
        publication.setProfile(profileProvider.getTarget(id));
        return publicationRepository.save(publication);
    }

    public List<Publication> getAllMy() {
        return publicationRepository.findAllByOwner(profileProvider.getUserFromAuth());
    }

    public List<Publication> getOnPage(Integer id) {
        Profile page = profileProvider.getTarget(id);
        return publicationRepository.findAllByProfile(page);
    }

}
