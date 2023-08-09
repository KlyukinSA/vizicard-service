package vizicard.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    public Device getDevice(String url) throws IOException {
        return deviceRepository.findByUrl(url);
    }

}
