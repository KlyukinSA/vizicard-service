package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.PublicationDTO;
import vizicard.dto.PublicationOnPageResponse;
import vizicard.service.PublicationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;
    private final ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public PublicationDTO createPublication(@RequestBody PublicationDTO dto) {
        return publicationService.createPublication(dto);
    }

    @GetMapping("my")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationDTO> getAllMy() {
        return publicationService.getAllMy();
    }

    @GetMapping("on-page")
    @PreAuthorize("isAuthenticated()")
    public List<PublicationOnPageResponse> getOnPage(@RequestParam Integer id) {
        return publicationService.getOnPage(id).stream()
                .map(e -> modelMapper.map(e, PublicationOnPageResponse.class))
                .collect(Collectors.toList());
    }

}
