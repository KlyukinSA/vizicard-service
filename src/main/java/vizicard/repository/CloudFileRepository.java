package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Album;
import vizicard.model.CloudFile;
import vizicard.model.CloudFileType;

import java.util.List;

public interface CloudFileRepository extends JpaRepository<CloudFile, Integer> {
    List<CloudFile> findAllByAlbumAndType(Album album, CloudFileType type);
}
