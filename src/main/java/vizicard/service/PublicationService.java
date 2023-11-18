package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.model.Card;
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
        publication.setAccountOwner(profileProvider.getUserFromAuth());
        publication.setCard(profileProvider.getTarget(id));
        return publicationRepository.save(publication);
    }

    public List<Publication> getAllMy() {
        return publicationRepository.findAllByAccountOwner(profileProvider.getUserFromAuth());
    }

    public List<Publication> getOnPage(Integer id) {
        Card page = profileProvider.getTarget(id);
        return publicationRepository.findAllByCard(page);
    }

    public boolean isUsualPublication(Publication publication) {
        return publication.getRating() == null;
    }

    public boolean isComment(Publication publication) {
        return !isUsualPublication(publication);
    }

}
