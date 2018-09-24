package example.micronaut;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import io.micronaut.context.annotation.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("aws")
public class AwsCredentialsConfigurationProperties implements AWSCredentialsProvider {

    @NotBlank
    private String key;

    @NotBlank
    private String secret;

    @Override
    public AWSCredentials getCredentials() {
        return new BasicAWSCredentials(getKey(), getSecret());
    }

    @Override
    public void refresh() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }
}

