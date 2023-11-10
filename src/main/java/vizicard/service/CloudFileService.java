package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vizicard.model.Album;
import vizicard.model.CloudFile;
import vizicard.repository.CloudFileRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloudFileService {

    private final CloudFileRepository cloudFileRepository;
    private final S3Service s3Service;
    private final EntityManager entityManager;

    public CloudFile saveFile(MultipartFile file, Album album) {
        String key = s3Service.uploadFile(file);
        return finishUrl(cloudFileRepository.save(new CloudFile(key, album)));
    }

    public CloudFile saveExternal(String url, Album album) {
        String key = s3Service.uploadExternal(url);
        return finishUrl(cloudFileRepository.save(new CloudFile(key, album)));
    }

    public CloudFile findById(Integer id) {
        return finishUrl(cloudFileRepository.findById(id).get());
    }

    public List<CloudFile> findAllByAlbum(Album album) {
        return cloudFileRepository.findAllByAlbum(album).stream()
                .map(this::finishUrl)
                .collect(Collectors.toList());
    }

    public CloudFile save(CloudFile file) {
        return finishUrl(cloudFileRepository.save(file));
    }

    private CloudFile finishUrl(CloudFile cloudFile) {
        cloudFile.setUrl(s3Service.getUrlFromKey(cloudFile.getUrl()));
        entityManager.detach(cloudFile);
        return cloudFile;
    }

}
