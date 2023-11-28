package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vizicard.exception.CustomException;
import vizicard.model.Album;
import vizicard.model.CloudFile;
import vizicard.model.CloudFileType;
import vizicard.repository.AlbumRepository;
import vizicard.repository.CloudFileRepository;
import vizicard.utils.RelationValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final CloudFileService cloudFileService;
    private final RelationValidator relationValidator;

    public CloudFile addFile(MultipartFile file, Integer id, CloudFileType type) {
        Album album = albumRepository.findById(id).get();
        relationValidator.stopNotOwnerOf(album.getCardOwner());
        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        if (type == CloudFileType.MEDIA
                && !(extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("png"))) {
            throw new CustomException("can add only jpg and png in media", HttpStatus.FORBIDDEN);
        }
        return cloudFileService.saveFile(file, album, type, extension);
    }

    public List<CloudFile> getAllFiles(Integer id, CloudFileType type) {
        Album album = albumRepository.findById(id).get();
        return cloudFileService.findAllByAlbumAndType(album, type).stream()
                .filter(CloudFile::isStatus)
                .collect(Collectors.toList());
    }

    public void deleteFile(Integer id) {
        CloudFile file = cloudFileService.findById(id);
        relationValidator.stopNotOwnerOf(file.getAlbum().getCardOwner());
        file.setStatus(false);
        cloudFileService.save(file);
    }

    public CloudFile addLinkFile(String url, Integer id) {
        Album album = albumRepository.findById(id).get();
        relationValidator.stopNotOwnerOf(album.getCardOwner());
        String extension = url.substring(url.lastIndexOf(".") + 1);
        return cloudFileService.saveLink(url, album, extension);
    }

}
