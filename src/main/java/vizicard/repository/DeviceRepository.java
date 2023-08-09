package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Device;

public interface DeviceRepository extends JpaRepository<Device, Integer> {
    Device findByUrl(String word);
}
