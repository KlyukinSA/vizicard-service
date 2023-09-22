package vizicard.service;

import com.amazonaws.services.macie2.model.ClassificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vizicard.model.Album;
import vizicard.model.CloudFile;
import vizicard.repository.AlbumRepository;
import vizicard.repository.CloudFileRepository;
import vizicard.utils.ProfileProvider;
import vizicard.utils.RelationValidator;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final CloudFileRepository cloudFileRepository;
    private final S3Service s3Service;
    private final RelationValidator relationValidator;

    public CloudFile addFile(MultipartFile file, Integer id) throws IOException {
        Album album = albumRepository.findById(id).get();
        relationValidator.stopNotOwnerOf(album.getOwner());
        String url = s3Service.uploadFile(file);
        return cloudFileRepository.save(new CloudFile(url, album));
    }

    public List<CloudFile> getAllFiles(Integer id) {
        Album album = albumRepository.findById(id).get();
        return cloudFileRepository.findAllByAlbum(album).stream()
                .filter(CloudFile::isStatus)
                .collect(Collectors.toList());
    }

    public void deleteFile(Integer id) {
        CloudFile file = cloudFileRepository.findById(id).get();
        relationValidator.stopNotOwnerOf(file.getAlbum().getOwner());
        file.setStatus(false);
        cloudFileRepository.save(file);
    }

}
