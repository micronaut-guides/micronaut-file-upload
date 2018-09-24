package example.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties("aws.s3")
public class S3ConfigurationProperties implements S3Configuration {

    @NotNull
    private String bucket;

    @NotNull
    private String region;

    @NotNull
    @Override
    public String getBucket() {
        return bucket;
    }

    public void setBucket(@NotNull String bucket) {
        this.bucket = bucket;
    }

    @NotNull
    @Override
    public String getRegion() {
        return region;
    }

    public void setRegion(@NotNull String region) {
        this.region = region;
    }
}
