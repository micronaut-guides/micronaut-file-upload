package example.micronaut;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.multipart.PartData;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
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
    public void upload(String key, StreamingFileUpload file) {
        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .withMultipartUploadThreshold((long) (5 * 1024 * 1025))
                .build();

        Flowable.fromPublisher(file)
                .forEach(partData -> {
                    ProgressListener progressListener = null;
                    if (LOG.isTraceEnabled()) {
                        progressListener = progressEvent -> LOG.trace("Transferred bytes: {}", String.valueOf(progressEvent.getBytesTransferred()));
                    }
                    PutObjectRequest request = new PutObjectRequest(
                            bucket, key, partData.getInputStream(), new ObjectMetadata());
                    if (progressListener == null) {
                        request.setGeneralProgressListener(progressListener);
                    }
                    Upload upload = tm.upload(request);
                    try {
                        upload.waitForCompletion();
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("Upload complete.");
                        }
                    } catch (AmazonClientException e) {
                        LOG.error("Error occurred while uploading file "+ e.getMessage());
                    }
                });
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
