package example.micronaut;

import javax.validation.constraints.NotNull;

public interface S3Configuration {

    @NotNull
    String getBucket();

    @NotNull
    String getRegion();
}
