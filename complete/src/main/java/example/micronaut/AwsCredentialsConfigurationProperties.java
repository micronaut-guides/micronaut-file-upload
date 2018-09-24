package example.micronaut;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws")
public class AwsCredentialsConfigurationProperties implements AWSCredentialsProvider {

    private String accessKeyId;

    private String secretKey;

    @Override
    public AWSCredentials getCredentials() {
        return new BasicAWSCredentials(getAccessKeyId(), getSecretKey());
    }

    @Override
    public void refresh() {

    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}

