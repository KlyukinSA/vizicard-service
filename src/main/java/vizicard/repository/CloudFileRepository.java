package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Album;
import vizicard.model.CloudFile;

import java.util.List;

public interface CloudFileRepository extends JpaRepository<CloudFile, Integer> {
    List<CloudFile> findAllByAlbum(Album album);
}
