package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.CloudFile;

public interface CloudFileRepository extends JpaRepository<CloudFile, Integer> {
}
