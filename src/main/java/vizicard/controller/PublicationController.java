package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.publication.PublicationCreateDTO;
import vizicard.dto.publication.PublicationResponse;
import vizicard.model.Profile;
import vizicard.model.Publication;
import vizicard.service.PublicationService;
import vizicard.utils.ProfileMapper;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;
    private final ModelMapper modelMapper;
    private final ProfileMapper profileMapper;

    @PostMapping("profiles/{id}/publications")
    @PreAuthorize("isAuthenticated()")
    public PublicationCreateDTO createPublication(@RequestBody PublicationCreateDTO dto, @PathVariable Integer id) {
        Publication map = modelMapper.map(dto, Publication.class);
        return modelMapper.map(publicationService.createPublication(map, id), PublicationCreateDTO.class);
    }

    @GetMapping("publications/my")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationResponse> getAllMy() {
        return getResponse(publicationService.getAllMy(), Publication::getProfile);
    }

    @GetMapping("profiles/{id}/publications")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationResponse> getOnPage(@PathVariable Integer id) {
        return getResponse(publicationService.getOnPage(id), Publication::getOwner);
    }

    private List<PublicationResponse> getResponse(List<Publication> list, Function<Publication, Profile> targetProvider) {
        return list.stream()
                .filter(publicationService::isUsualPublication)
                .map(e -> {
                    PublicationResponse dto = modelMapper.map(e, PublicationResponse.class);
                    dto.setProfile(profileMapper.mapToBrief(targetProvider.apply(e)));
                    return dto;})
                .collect(Collectors.toList());
    }

}
