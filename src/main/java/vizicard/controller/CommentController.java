package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.CommentOfMineResponse;
import vizicard.dto.publication.CommentCreateDTO;
import vizicard.dto.publication.CommentResponse;
import vizicard.model.Profile;
import vizicard.model.Publication;
import vizicard.service.PublicationService;
import vizicard.utils.ProfileMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Validated
@RequiredArgsConstructor
public class CommentController {

    private final ModelMapper modelMapper;
    private final PublicationService publicationService;
    private final ProfileMapper profileMapper;

    @PostMapping("profiles/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    public CommentCreateDTO createComment(@Valid @RequestBody CommentCreateDTO dto, @PathVariable Integer id) {
        return modelMapper.map(publicationService.createPublication(modelMapper.map(dto, Publication.class), id), CommentCreateDTO.class);
    }

    @GetMapping("comments/my")
    @PreAuthorize("isAuthenticated()")
    public List<CommentOfMineResponse> getAllMy() {
        return (List<CommentOfMineResponse>) getResponse(publicationService.getAllMy(), Publication::getProfile, CommentOfMineResponse.class);
    }

    @GetMapping("profiles/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    public List<CommentResponse> getOnPage(@PathVariable Integer id) {
        return (List<CommentResponse>) getResponse(publicationService.getOnPage(id).stream().filter(Publication::isModerated).collect(Collectors.toList()), Publication::getOwner, CommentResponse.class);
    }
    // TODO publication/comment response list mapper
    private List<? extends CommentResponse> getResponse(List<Publication> list, Function<Publication, Profile> targetProvider, Class responseClass) {
        return list.stream()
                .filter(publicationService::isComment)
                .map(e -> {
                    CommentResponse dto = (CommentResponse) modelMapper.map(e, responseClass);
                    dto.setProfile(profileMapper.mapToBrief(targetProvider.apply(e)));
                    return dto;})
                .collect(Collectors.toList());
    }

}

