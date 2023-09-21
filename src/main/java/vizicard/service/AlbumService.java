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
import java.util.Objects;
import java.util.Optional;

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

}
