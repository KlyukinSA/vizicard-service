package vizicard.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.CloudFileDTO;
import vizicard.model.CloudFile;
import vizicard.repository.CloudFileRepository;
import vizicard.utils.ProfileProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName = "2cc1de15-bc1f377d-9e5a-448f-8a1d-f117b93916d2";
    private final CloudFileRepository cloudFileRepository;

    public String uploadFile(final MultipartFile file) throws AmazonClientException, IOException {
        String keyName = String.valueOf(UUID.randomUUID());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        s3Client.putObject(bucketName, keyName, file.getInputStream(), metadata);

        URL url = s3Client.getUrl(bucketName, keyName);
//        System.out.println(url);

        return url.toString();
    }

    public String getUrlById(Integer id) {
        return cloudFileRepository.getById(id).getUrl();
    }

    public CloudFile getById(Integer id) {
        return cloudFileRepository.findById(id).get();
    }

}
