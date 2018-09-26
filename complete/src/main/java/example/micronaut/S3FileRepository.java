package example.micronaut;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.multipart.PartData;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executors;

@Requires(beans = S3Configuration.class)
@Requires(beans = AwsCredentialsConfigurationProperties.class)
@Singleton // <1>
public class S3FileRepository implements FileRepository, Closeable {
    protected static final Logger LOG = LoggerFactory.getLogger(S3FileRepository.class);

    private final String bucket;
    private final AmazonS3 s3Client;
    private final TransferManager tm;

    public S3FileRepository(S3Configuration s3Configuration,
                            AwsCredentialsConfigurationProperties awsCredentialsConfigurationProperties) { // <2>
        this.bucket = s3Configuration.getBucket();
        s3Client = AmazonS3Client.builder()
                .withRegion(s3Configuration.getRegion())
                .withCredentials(awsCredentialsConfigurationProperties)
                .build();
        long multipartUploadThreshold = s3Configuration.getMultipartUploadThreshold();
        int maxUploadThreads = s3Configuration.getMaxUploadThreads();
        tm = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .withMultipartUploadThreshold(multipartUploadThreshold)
                .withExecutorFactory(() -> Executors.newFixedThreadPool(maxUploadThreads))
                .build();
    }

    public boolean doesObjectExists(String key) {
        return s3Client.doesObjectExist(bucket, key);
    }

    @Override
    public void upload(String key, CompletedFileUpload file) {
        try {
            InputStream inputStream = file.getInputStream();
            PutObjectRequest request = new PutObjectRequest(bucket, key, inputStream, new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(request);
            inputStream.close();
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error occurred while uploading file "+ e.getMessage());
            }
        }
    }

    @Override
    public void upload(String key, StreamingFileUpload file) {
        Flowable.fromPublisher(file)
                .map(partData -> {
                    PutObjectRequest request = new PutObjectRequest(bucket, key, partData.getInputStream(), new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead);
                    return tm.upload(request);
                })
                //.doOnTerminate(tm::shutdownNow)
                .subscribe(upload -> {
                   do {
                   } while(!upload.isDone());
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

    @Override
    public void close() throws IOException {
        tm.shutdownNow();
    }
}
