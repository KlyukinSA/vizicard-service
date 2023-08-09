package vizicard.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.DeviceDTO;
import vizicard.model.Device;
import vizicard.repository.DeviceRepository;

import java.io.IOException;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceRepository deviceRepository;
    //Получение девайса по url.
    @GetMapping
    public DeviceDTO getDevice(String url) throws IOException {
        Device device = deviceRepository.findByUrl(url);
        return new DeviceDTO(device.getId(), device.getOwner().getId(), device.getUrl());
    }

}
