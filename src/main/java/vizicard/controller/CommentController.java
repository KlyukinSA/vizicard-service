package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.CommentOfMineResponse;
import vizicard.dto.publication.CommentCreateDTO;
import vizicard.dto.publication.CommentResponse;
import vizicard.mapper.PublicationCommentResponseMapper;
import vizicard.model.Card;
import vizicard.model.Publication;
import vizicard.service.PublicationService;
import vizicard.mapper.CardMapper;

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
    private final PublicationCommentResponseMapper publicationCommentResponseMapper;

    @PostMapping("profiles/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    public CommentCreateDTO createComment(@Valid @RequestBody CommentCreateDTO dto, @PathVariable Integer id) {
        return modelMapper.map(publicationService.createPublication(modelMapper.map(dto, Publication.class), id), CommentCreateDTO.class);
    }

    @GetMapping("comments/my")
    @PreAuthorize("isAuthenticated()")
    public List<CommentOfMineResponse> getAllMy() {
        return (List<CommentOfMineResponse>) publicationCommentResponseMapper.getResponse(publicationService.getAllMy(), Publication::getCard, CommentOfMineResponse.class);
    }

    @GetMapping("profiles/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    public List<CommentResponse> getOnPage(@PathVariable Integer id) {
        return (List<CommentResponse>) publicationCommentResponseMapper.getResponse(publicationService.getOnPage(id).stream().filter(Publication::isModerated).collect(Collectors.toList()), p -> p.getOwner().getMainCard(), CommentResponse.class);
    }

}

