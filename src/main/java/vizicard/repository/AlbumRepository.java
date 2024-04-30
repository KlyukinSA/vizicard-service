package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Album;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
}
