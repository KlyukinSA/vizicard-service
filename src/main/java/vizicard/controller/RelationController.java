package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.RelationResponseDTO;
import vizicard.service.ProfileService;
import vizicard.service.RelationService;

import javax.imageio.spi.RegisterableService;
import java.util.List;

@RestController
@RequestMapping("/relations")
@RequiredArgsConstructor
public class RelationController {

    private final RelationService relationService;

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public void unrelate(@RequestParam(required = false) Integer owner, @RequestParam Integer profile) {
        relationService.unrelate(owner, profile);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public List<RelationResponseDTO> getRelations() {
        return relationService.getRelations();
    }

}
