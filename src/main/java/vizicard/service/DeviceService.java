package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.dto.DeviceDTO;
import vizicard.model.Device;
import vizicard.repository.DeviceRepository;
import vizicard.utils.ProfileProvider;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final ProfileProvider profileProvider;

    public DeviceDTO findByUrl(String url) {
        Device device = deviceRepository.findByUrl(url);
        return new DeviceDTO(device.getId(), device.getOwner().getId(), device.getUrl());
    }

    public boolean addDevice(String url) {
        Device device = deviceRepository.findByUrl(url);
        if (device == null) {
            deviceRepository.save(new Device(profileProvider.getUserFromAuth(), url));
            return true;
        }
        return false;
    }

}
