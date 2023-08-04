package vizicard.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;
    private final String bucketName = "2cc1de15-bc1f377d-9e5a-448f-8a1d-f117b93916d2";

    public void uploadFile(
            final String keyName,
            final Long contentLength,
            final String contentType,
            final InputStream value
    ) throws AmazonClientException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);

        s3Client.putObject(bucketName, keyName, value, metadata);
        System.out.println("File uploaded to bucket({}): {}" + bucketName + keyName);
    }

}
