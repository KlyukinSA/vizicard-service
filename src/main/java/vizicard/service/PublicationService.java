package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vizicard.dto.PublicationCreateDTO;
import vizicard.model.Profile;
import vizicard.model.Publication;
import vizicard.repository.PublicationRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;

    public PublicationCreateDTO createPublication(PublicationCreateDTO dto) {
        Publication publication = new Publication(profileProvider.getUserFromAuth());
        modelMapper.map(dto, publication);
        publicationRepository.save(publication);
        return dto;
    }

    public List<Publication> getAllMy() {
        return publicationRepository.findAllByOwner(profileProvider.getUserFromAuth());
    }

    public List<Publication> getOnPage(Integer id) {
        Profile page = profileProvider.getTarget(id);
        return publicationRepository.findAllByProfile(page);
    }

}
