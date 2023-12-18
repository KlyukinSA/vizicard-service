package vizicard.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vizicard.model.CloudFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName = "2cc1de15-bc1f377d-9e5a-448f-8a1d-f117b93916d2";

    @SneakyThrows
    public CloudFile uploadFile(final MultipartFile file, int quality, String extension) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        InputStream src = file.getInputStream();
        if (quality > 0) {
            src = resize(src, quality, extension, metadata);
        }
        return upload(src, metadata, metadata.getContentLength());
    }

    private InputStream resize(InputStream src, int quality, String extension, ObjectMetadata metadata) throws IOException {
        BufferedImage read = ImageIO.read(src);
        BufferedImage resizedImage = resizeImage(read, cut(read.getWidth(), quality), cut(read.getHeight(), quality));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, extension, os);
        metadata.setContentLength(os.size());
        return new ByteArrayInputStream(os.toByteArray());
    }

    private int cut(int length, int quality) {
        double[] scales = {1, 1.5, 2.5, 3.5};
        return (int) (length / scales[quality]);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    @SneakyThrows
    public String uploadExternal(String url) {
        InputStream in = new BufferedInputStream(new URL(url).openStream());
        return upload(in, new ObjectMetadata(), 0).getUrl();
    }

    private CloudFile upload(InputStream in, ObjectMetadata metadata, long size) {
        String keyName = String.valueOf(UUID.randomUUID());
        s3Client.putObject(bucketName, keyName, in, metadata);
        CloudFile res = new CloudFile();
        res.setUrl(keyName);
        res.setSize(size);
        return res;
    }

    public String getUrlFromKey(String keyName) {
        return String.valueOf(s3Client.getUrl(bucketName, keyName));
    }

}
