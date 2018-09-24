package example.micronaut;

import io.micronaut.http.multipart.CompletedFileUpload;
import java.net.URL;

public interface FileRepository {

    void upload(CompletedFileUpload file, String key);

    void delete(String key);

    URL findURLbyKey(String key);
}
