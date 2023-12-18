package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vizicard.exception.CustomException;
import vizicard.model.Card;
import vizicard.model.CloudFile;
import vizicard.model.CloudFileType;
import vizicard.repository.AlbumRepository;
import vizicard.utils.RelationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final CloudFileService cloudFileService;
    private final RelationValidator relationValidator;

    public CloudFile addFile(Card card, MultipartFile file, CloudFileType type) {
        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        if (type == CloudFileType.MEDIA
                && !(extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("png"))) {
            throw new CustomException("can add only jpg and png in media", HttpStatus.FORBIDDEN);
        }
        return cloudFileService.saveFile(file, card.getAlbum(), type, extension);
    }

    public List<CloudFile> addScaledPhotos(Card card, MultipartFile file, int amount) {
        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        if (!(extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("png"))) {
            throw new CustomException("can add only jpg and png in media", HttpStatus.FORBIDDEN);
        }
        List<CloudFile> res = new ArrayList<>();
        for (int quality = 0; quality < amount; quality++) {
            res.add(cloudFileService.saveScaledPhoto(file, card.getAlbum(), extension, quality));
        }
        return res;
    }

    public List<CloudFile> getAllFiles(Card card, CloudFileType type) {
        return cloudFileService.findAllByAlbumAndType(card.getAlbum(), type).stream()
                .filter(CloudFile::isStatus)
                .collect(Collectors.toList());
    }

    public void deleteFile(Integer id) {
        CloudFile file = cloudFileService.findById(id);
        relationValidator.stopNotOwnerOf(file.getAlbum().getCardOwner());
        file.setStatus(false);
        cloudFileService.save(file);
    }

    public CloudFile addLinkFile(Card card, String url) {
        String extension = url.substring(url.lastIndexOf(".") + 1);
        return cloudFileService.saveLink(url, card.getAlbum(), extension);
    }

}
