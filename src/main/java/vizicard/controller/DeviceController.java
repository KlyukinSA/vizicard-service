package vizicard.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.DeviceDTO;
import vizicard.model.Device;
import vizicard.repository.DeviceRepository;
import vizicard.service.DeviceService;
import vizicard.utils.ProfileProvider;

import java.io.IOException;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public DeviceDTO findByUrl(String url) throws IOException {
        return deviceService.findByUrl(url);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public boolean addDevice(@RequestParam String url) {
        return deviceService.addDevice(url);
    }

}
