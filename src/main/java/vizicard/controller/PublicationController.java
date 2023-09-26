package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.PublicationCreateDTO;
import vizicard.dto.PublicationResponse;
import vizicard.model.Profile;
import vizicard.model.Publication;
import vizicard.service.PublicationService;
import vizicard.utils.ProfileMapper;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;
    private final ModelMapper modelMapper;
    private final ProfileMapper profileMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public PublicationCreateDTO createPublication(@RequestBody PublicationCreateDTO dto) {
        return publicationService.createPublication(dto);
    }

    @GetMapping("my")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationResponse> getAllMy() {
        return getResponse(publicationService.getAllMy(), Publication::getProfile);
    }

    @GetMapping("on-page")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationResponse> getOnPage(@RequestParam Integer id) {
        return getResponse(publicationService.getOnPage(id), Publication::getOwner);
    }

    private List<PublicationResponse> getResponse(List<Publication> list, Function<Publication, Profile> targetProvider) {
        return list.stream()
                .map(e -> {
                    PublicationResponse dto = modelMapper.map(e, PublicationResponse.class);
                    dto.setProfile(profileMapper.mapToBrief(targetProvider.apply(e)));
                    return dto;})
                .collect(Collectors.toList());
    }
}
