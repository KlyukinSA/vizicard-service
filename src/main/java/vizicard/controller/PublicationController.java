package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.publication.PublicationCreateDTO;
import vizicard.dto.publication.PublicationResponse;
import vizicard.mapper.PublicationCommentResponseMapper;
import vizicard.model.Publication;
import vizicard.service.PublicationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;
    private final ModelMapper modelMapper;
    private final PublicationCommentResponseMapper publicationCommentResponseMapper;

    @PostMapping("profiles/{id}/publications")
    @PreAuthorize("isAuthenticated()")
    public PublicationCreateDTO createPublication(@RequestBody PublicationCreateDTO dto, @PathVariable Integer id) {
        Publication map = modelMapper.map(dto, Publication.class);
        return modelMapper.map(publicationService.createPublication(map, id), PublicationCreateDTO.class);
    }

    @GetMapping("publications/my")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationResponse> getAllMy() {
        return (List<PublicationResponse>) publicationCommentResponseMapper.getResponse(publicationService.getAllMy(), Publication::getCard, PublicationResponse.class);
    }

    @GetMapping("profiles/{id}/publications")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationResponse> getOnPage(@PathVariable Integer id) {
        return (List<PublicationResponse>) publicationCommentResponseMapper.getResponse(publicationService.getOnPage(id), p -> p.getAccountOwner().getMainCard(), PublicationResponse.class);
    }

}
