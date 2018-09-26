package example.micronaut;

import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller("/")
public class HomeController {

    public static final String IMAGE_KEY = "applogo.png";
    private final FileRepository fileRepository;
    protected static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    public HomeController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @View("home")
    @Get
    public Map<String, Object> index() {
        return homeModel();
    }

    @View("home")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Post("/upload")
    public HttpResponse upload(CompletedFileUpload file, @Header("Content-Length") String contentLength) {
        if ((file.getFilename() == null || file.getFilename().equals(""))) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("file name is not set");
            }
            return HttpResponse.seeOther(URI.create("/"));
        }
        fileRepository.upload(IMAGE_KEY, file);
        return HttpResponse.seeOther(URI.create("/"));
    }

    private Map<String, Object> homeModel() {
        Map<String, Object> model = new HashMap<>();
        if (fileRepository.doesObjectExists(IMAGE_KEY)) {
            model.put("imageurl", fileRepository.findURLbyKey(IMAGE_KEY).toString());
        }
        model.put("key", IMAGE_KEY);
        return model;
    }

    @View("home")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Post("/uploadstreaming")
    public HttpResponse uploadstreaming(StreamingFileUpload file) {
        fileRepository.upload(IMAGE_KEY, file);
        return HttpResponse.seeOther(URI.create("/"));
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/delete")
    public HttpResponse delete(String key) {
        fileRepository.delete(key);
        return HttpResponse.seeOther(URI.create("/"));
    }
}
