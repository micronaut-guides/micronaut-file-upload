package example.micronaut;

//tag::imports[]
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.multipart.FileUpload;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executors;
//end::imports[]

//tag::clazz[]
@Requires(beans = S3Configuration.class) // <1>
@Requires(beans = AwsCredentialsConfigurationProperties.class) // <1>
@Singleton // <2>
public class S3FileRepository implements FileRepository, Closeable { // <3>
//end::clazz[]

    //tag::propertys3client[]
    protected static final Logger LOG = LoggerFactory.getLogger(S3FileRepository.class);

    private final String bucketName;
    private final AmazonS3 s3Client;
    //end::propertys3client[]

    //tag::tm[]
    private final TransferManager tm;
    //end::tm[]

    //tag::constructor[]
    public S3FileRepository(S3Configuration s3Configuration,
                            AwsCredentialsConfigurationProperties awsCredentialsConfigurationProperties) { // <4>
    //end::constructor[]

        //tag::constructors3client[]
        this.bucketName = s3Configuration.getBucket();
        s3Client = AmazonS3Client.builder()
                .withRegion(s3Configuration.getRegion())
                .withCredentials(awsCredentialsConfigurationProperties)
                .build();
        //end::constructors3client[]

        //tag::constructortm[]
        long multipartUploadThreshold = s3Configuration.getMultipartUploadThreshold();
        int maxUploadThreads = s3Configuration.getMaxUploadThreads();
        tm = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .withMultipartUploadThreshold(multipartUploadThreshold)
                .withExecutorFactory(() -> Executors.newFixedThreadPool(maxUploadThreads))
                .build();
        //end::constructortm[]
    }

    //tag::doesObjectExists[]
    @Override
    public boolean doesObjectExists(String key) {
        return s3Client.doesObjectExist(bucketName, key);
    }
    //end::doesObjectExists[]

    //tag::uploadcompleted[]
    @Override
    public void upload(String key, CompletedFileUpload file) {
        try {
            InputStream inputStream = file.getInputStream();
            PutObjectRequest request = new PutObjectRequest(bucketName,
                    key,
                    inputStream,
                    createObjectMetadata(file)).withCannedAcl(CannedAccessControlList.PublicRead); // <5>
            s3Client.putObject(request);
            inputStream.close();
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error occurred while uploading file "+ e.getMessage());
            }
        }
    }
    //end::uploadcompleted[]

    //tag::uploadstreaming[]
    @Override
    public void upload(String keyName, StreamingFileUpload file) {

        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);
        s3Client.initiateMultipartUpload(initRequest);

        Flowable.fromPublisher(file)
                .map(partData -> {
                    InputStream inputStream = partData.getInputStream();
                    PutObjectRequest request = new PutObjectRequest(bucketName,
                            keyName,
                            inputStream,
                            createObjectMetadata(file)).withCannedAcl(CannedAccessControlList.PublicRead);
                    inputStream.close();
                    return tm.upload(request);
                })
                .subscribe(upload -> {
                   do {
                   } while(!upload.isDone());
                });
    }
    //end::uploadstreaming[]

    //tag::findURLbyKey[]
    @Override
    public URL findURLbyKey(String key) {
        return s3Client.getUrl(bucketName, key);
    }
    //end::findURLbyKey[]

    //tag::delete[]
    @Override
    public void delete(String key) {
        s3Client.deleteObject(bucketName, key);
    }
    //end::delete[]

    //tag::close[]
    @Override
    public void close() throws IOException {
        tm.shutdownNow();
    }
    //end::close[]

    //tag::createObjectMetadata[]
    private ObjectMetadata createObjectMetadata(FileUpload file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        file.getContentType().ifPresent(contentType -> objectMetadata.setContentType(contentType.getName()));
        if (file.getSize() != 0) {
            objectMetadata.setContentLength(file.getSize());
        }
        return objectMetadata;
    }
    //end::createObjectMetadata[]
}
