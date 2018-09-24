package example.micronaut;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller("/")
public class HomeController {

    private final FileRepository fileRepository;
    protected static final Logger LOG = LoggerFactory.getLogger(S3FileRepository.class);

    public HomeController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @View("home")
    @Get("/")
    public Map<String, Object> index() {
        return Collections.emptyMap();
    }

    @View("home")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Post("/upload")
    public Map<String, Object> upload(CompletedFileUpload file) {
        final String key = file.getFilename();
        fileRepository.upload(file, key);
        URL fileURL = fileRepository.findURLbyKey(key);
        Map<String, Object> model = new HashMap<>();
        model.put("imageurl", fileURL.toString());
        model.put("key", key);
        return model;
    }

    @View("home")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/delete")
    public Map<String, Object> delete(String key) {
        fileRepository.delete(key);
        return Collections.emptyMap();
    }

}
