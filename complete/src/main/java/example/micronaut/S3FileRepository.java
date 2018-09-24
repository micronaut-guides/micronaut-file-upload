package example.micronaut;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.multipart.CompletedFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Requires(beans = S3Configuration.class)
@Requires(beans = AwsCredentialsConfigurationProperties.class)
@Singleton // <1>
public class S3FileRepository implements FileRepository {
    protected static final Logger LOG = LoggerFactory.getLogger(S3FileRepository.class);

    private final String bucket;
    private final AmazonS3 s3Client;

    public S3FileRepository(S3Configuration s3Configuration,
                            AwsCredentialsConfigurationProperties awsCredentialsConfigurationProperties) { // <2>
        this.bucket = s3Configuration.getBucket();
        s3Client = AmazonS3Client.builder()
                .withRegion(s3Configuration.getRegion())
                .withCredentials(awsCredentialsConfigurationProperties)
                .build();
    }

    @Override
    public void upload(CompletedFileUpload file, String key) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Uploading {} S3 bucket  {} ", key, bucket);
        }
        try {

            final int length = file.getBytes().length;
            if (LOG.isDebugEnabled()) {
                LOG.debug("content-length {}", length);
            }
            ObjectMetadata s3ObjectMetadata = new ObjectMetadata();
            s3ObjectMetadata.setContentLength(length);
            s3Client.putObject(new PutObjectRequest(bucket, key, file.getInputStream(), s3ObjectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (AmazonServiceException | IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getMessage());
            }
        }
    }

    @Override
    public URL findURLbyKey(String key) {
        return s3Client.getUrl(bucket, key);
    }

    @Override
    public void delete(String key) {
        s3Client.deleteObject(bucket, key);
    }
}
