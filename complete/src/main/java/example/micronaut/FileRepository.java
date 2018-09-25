package example.micronaut;

import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.multipart.StreamingFileUpload;

import java.net.URL;

public interface FileRepository {

    boolean doesObjectExists(String key);

    void delete(String key);

    URL findURLbyKey(String key);

    void upload(String key, StreamingFileUpload file);

    void upload(String key, CompletedFileUpload file);
}
