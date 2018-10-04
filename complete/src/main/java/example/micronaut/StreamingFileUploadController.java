package example.micronaut;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.views.View;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller("/create")
public class StreamingFileUploadController {

    public static final String REDIRECT_PATH = "/create";

    public static String IMAGE_KEY = "streamingapplogo.png";

    private final FileRepository fileRepository;

    public StreamingFileUploadController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @View("create")
    @Get
    Map<String, Object> index() {
        return new HashMap<>();
    }

    //tag::uploadstreaming[]
    @Consumes(MediaType.MULTIPART_FORM_DATA) // <1>
    @Post("/upload") // <2>
    public HttpResponse upload(StreamingFileUpload file) {
        fileRepository.upload(IMAGE_KEY, file);
        return HttpResponse.seeOther(URI.create(REDIRECT_PATH)); // <3>
    }
    //end::uploadstreaming[]

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED) // <5>
    @Post("/delete") // <6>
    public HttpResponse delete(String key) {
        fileRepository.delete(key);
        return HttpResponse.seeOther(URI.create(REDIRECT_PATH)); // <7>
    }
}
