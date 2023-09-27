package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.CommentCreateDTO;
import vizicard.dto.PublicationResponse;
import vizicard.model.Profile;
import vizicard.model.Publication;
import vizicard.service.PublicationService;
import vizicard.utils.ProfileMapper;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final ModelMapper modelMapper;
    private final PublicationService publicationService;
    private final ProfileMapper profileMapper;

    @PostMapping("profiles/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    public CommentCreateDTO createComment(@RequestBody CommentCreateDTO dto, @PathVariable Integer id) {
        return modelMapper.map(publicationService.createPublication(modelMapper.map(dto, Publication.class), id), CommentCreateDTO.class);
    }

    @GetMapping("comments/my")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationResponse> getAllMy() {
        return getResponse(publicationService.getAllMy(), Publication::getProfile);
    }

    @GetMapping("profiles/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationResponse> getOnPage(@PathVariable Integer id) {
        return getResponse(publicationService.getOnPage(id), Publication::getOwner);
    }
    // TODO publication response list mapper
    private List<PublicationResponse> getResponse(List<Publication> list, Function<Publication, Profile> targetProvider) {
        return list.stream()
                .filter(publicationService::isComment)
                .map(e -> {
                    PublicationResponse dto = modelMapper.map(e, PublicationResponse.class);
                    dto.setProfile(profileMapper.mapToBrief(targetProvider.apply(e)));
                    return dto;})
                .collect(Collectors.toList());
    }

}

