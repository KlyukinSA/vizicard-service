package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vizicard.model.Album;
import vizicard.model.CloudFile;
import vizicard.model.CloudFileType;
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
    private final ExtensionService extensionService;

    public CloudFile saveFile(MultipartFile file, Album album, CloudFileType type, String extension) {
        int quality = 0;
        CloudFile cloudFile = s3Service.uploadFile(file, quality, extension);
        return finishUrl(cloudFileRepository.save(new CloudFile(cloudFile.getUrl(), album, type, extensionService.getByName(extension), cloudFile.getSize(), quality)));
    }

    public CloudFile saveExternal(String url, Album album, CloudFileType type) {
        String key = s3Service.uploadExternal(url);
        String ext = url.substring(url.lastIndexOf(".") + 1);
        return finishUrl(cloudFileRepository.save(new CloudFile(key, album, type, extensionService.getByName(ext), 0, 0)));
    }

    public CloudFile findById(Integer id) {
        return finishUrl(cloudFileRepository.findById(id).get());
    }

    public List<CloudFile> findAllByAlbumAndType(Album album, CloudFileType type) {
        return cloudFileRepository.findAllByAlbumAndType(album, type).stream()
                .map(this::finishUrl)
                .collect(Collectors.toList());
    }

    public CloudFile save(CloudFile file) {
        return finishUrl(cloudFileRepository.save(file));
    }

    private CloudFile finishUrl(CloudFile cloudFile) {
        if (cloudFile.getType() != CloudFileType.LINK) {
            cloudFile.setUrl(s3Service.getUrlFromKey(cloudFile.getUrl()));
            entityManager.detach(cloudFile);
        }
        return cloudFile;
    }

    public CloudFile updateDescription(Integer id, String description) {
        CloudFile cloudFile = cloudFileRepository.findById(id).get();
        cloudFile.setDescription(description);
        cloudFileRepository.save(cloudFile);
        return finishUrl(cloudFile);
    }

    public CloudFile saveLink(String url, Album album, String extension) {
        return cloudFileRepository.save(new CloudFile(url, album, CloudFileType.LINK, extensionService.getByName(extension), 0, 0));
    }

    public CloudFile saveScaledPhoto(MultipartFile file, Album album, String extension, int quality) {
        CloudFile cloudFile = s3Service.uploadFile(file, quality, extension);
        return finishUrl(cloudFileRepository.save(new CloudFile(cloudFile.getUrl(), album, CloudFileType.MEDIA, extensionService.getByName(extension), cloudFile.getSize(), quality)));
    }

}
