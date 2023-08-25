package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.PageActionDTO;
import vizicard.service.ActionService;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    @PostMapping("clicks")
    void addClickAction(@RequestParam Integer id) {
        actionService.addClickAction(id);
    }

    @GetMapping("me")
    @PreAuthorize("isAuthenticated()")
    PageActionDTO getPageStats() {
        return actionService.getPageStats();
    }

}
