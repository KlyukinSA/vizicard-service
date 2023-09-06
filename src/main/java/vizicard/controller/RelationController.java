package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.LeadGenerationDTO;
import vizicard.dto.ProfileCreateDTO;
import vizicard.dto.RelationResponseDTO;
import vizicard.service.ProfileService;
import vizicard.service.RelationService;
import vizicard.utils.ProfileMapper;

import javax.imageio.spi.RegisterableService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/relations")
@RequiredArgsConstructor
public class RelationController {

    private final RelationService relationService;
    private final ProfileMapper profileMapper;

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public void unrelate(@RequestParam(required = false) Integer owner, @RequestParam Integer profile) {
        relationService.unrelate(owner, profile);
    }

    @PostMapping
    public ResponseEntity<?> relate(@RequestParam Integer id) throws Exception {
        return relationService.relate(id);
    }

    @PostMapping("/lead")
    public void leadGenerate(@RequestParam Integer id, @RequestBody(required = false) ProfileCreateDTO dto) {
        relationService.leadGenerate(id, dto);
    }

    @GetMapping
    public List<RelationResponseDTO> searchLike(@RequestParam(required = false) String name, @RequestParam(required = false) String type) {
        return relationService.searchLike(name, type).stream()
                .map((r) -> new RelationResponseDTO(profileMapper.mapToBrief(r.getProfile()), r.getCreateAt(), r.getType()))
                .collect(Collectors.toList());
    }

}
