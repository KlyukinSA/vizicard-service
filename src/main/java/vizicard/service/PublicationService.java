package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vizicard.dto.PublicationDTO;
import vizicard.model.Profile;
import vizicard.model.Publication;
import vizicard.repository.PublicationRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;

    public PublicationDTO createPublication(PublicationDTO dto) {
        Publication publication = new Publication(profileProvider.getUserFromAuth());
        modelMapper.map(dto, publication);
        publicationRepository.save(publication);
        return dto;
    }

    public List<PublicationDTO> getAllMy() {
        return publicationRepository.findAllByOwner(profileProvider.getUserFromAuth()).stream()
                .map((val) -> modelMapper.map(val, PublicationDTO.class))
                .collect(Collectors.toList());
    }

    public List<Publication> getOnPage(Integer id) {
        Profile page = profileProvider.getTarget(id);
        return publicationRepository.findAllByProfile(page);
    }

}
