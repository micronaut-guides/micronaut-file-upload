package example.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ConfigurationProperties("aws.s3")
public class S3ConfigurationProperties implements S3Configuration {

    @NotBlank
    private String bucket;

    @NotBlank
    private String region;

    @NotNull
    private Long multipartUploadThreshold;

    @NotNull
    private Integer maxUploadThreads;

    @Override
    public @NotBlank String getBucket() {
        return bucket;
    }

    @Override
    public @NotBlank String getRegion() {
        return region;
    }

    @Override
    public @NotNull Long getMultipartUploadThreshold() {
        return multipartUploadThreshold;
    }

    public void setBucket(String bucket) {
        if (bucket != null) {
            this.bucket = bucket;
        }
    }

    public void setRegion(String region) {
        if (region != null) {
            this.region = region;
        }
    }

    public void setMultipartUploadThreshold(Long multipartUploadThreshold) {
        if (multipartUploadThreshold != null) {
            this.multipartUploadThreshold = multipartUploadThreshold;
        }
    }

    @Override
    public @NotNull Integer getMaxUploadThreads() {
        return maxUploadThreads;
    }

    public void setMaxUploadThreads(Integer maxUploadThreads) {
        if (maxUploadThreads!=null) {
            this.maxUploadThreads = maxUploadThreads;
        }
    }
}
