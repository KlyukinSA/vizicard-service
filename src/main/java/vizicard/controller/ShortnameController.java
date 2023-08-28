package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.DeviceDTO;
import vizicard.dto.ShortnameDTO;
import vizicard.service.ShortnameService;

import java.io.IOException;

@RestController
@RequestMapping("/shortnames")
@RequiredArgsConstructor
public class ShortnameController {

    private final ShortnameService shortnameService;

    @GetMapping
    public DeviceDTO findByShortname(String shortname) throws IOException {
        return shortnameService.findByShortname(shortname);
    }

    @PostMapping("my")
    @PreAuthorize("isAuthenticated()")
    public boolean add(@RequestBody ShortnameDTO dto) {
        return shortnameService.add(dto);
    }

}
