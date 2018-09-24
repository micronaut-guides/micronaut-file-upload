package example.micronaut;

import io.micronaut.http.multipart.StreamingFileUpload;

import java.net.URL;

public interface FileRepository {

    void delete(String key);

    URL findURLbyKey(String key);

    void upload(String key, StreamingFileUpload file);
}
