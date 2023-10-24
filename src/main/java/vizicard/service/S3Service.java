package vizicard.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName = "2cc1de15-bc1f377d-9e5a-448f-8a1d-f117b93916d2";

    @SneakyThrows
    public String uploadFile(final MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        return upload(file.getInputStream(), metadata);
    }

    @SneakyThrows
    public String uploadExternal(String url) {
        InputStream in = new BufferedInputStream(new URL(url).openStream());
        return upload(in, new ObjectMetadata());
    }

    private String upload(InputStream in, ObjectMetadata metadata) {
        String keyName = String.valueOf(UUID.randomUUID());
        s3Client.putObject(bucketName, keyName, in, metadata);
        return keyName;
    }

    public String getUrlFromKey(String keyName) {
        return String.valueOf(s3Client.getUrl(bucketName, keyName));
    }

}
