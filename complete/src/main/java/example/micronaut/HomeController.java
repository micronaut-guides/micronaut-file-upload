package example.micronaut;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public Map<String, Object> upload(StreamingFileUpload file) {
        fileRepository.upload("blacklogo.png", file);
        Map<String, Object> model = new HashMap<>();
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
