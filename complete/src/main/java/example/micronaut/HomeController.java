package example.micronaut;

//tag::imports[]
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.views.View;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
//end::imports[]

//tag::clazz[]
@Controller("/") // <1>
public class HomeController {
//end::clazz[]

    //tag::di[]
    public static final String REDIRECT_PATH = "/";

    public static String IMAGE_KEY = "applogo.png";

    private final FileRepository fileRepository;

    public HomeController(FileRepository fileRepository) {  // <2>
        this.fileRepository = fileRepository;
    }
    //end::di[]

    //tag::index[]
    @View("home") // <3>
    @Get // <4>
    public Map<String, Object> index() {
        return model(IMAGE_KEY);
    }
    //end::index[]

    //tag::upload[]
    @Consumes(MediaType.MULTIPART_FORM_DATA) // <5>
    @Post("/upload") // <6>
    public HttpResponse upload(CompletedFileUpload file) { // <7>
        if ((file.getFilename() == null || file.getFilename().equals(""))) {
            return HttpResponse.seeOther(URI.create(REDIRECT_PATH));
        }
        fileRepository.upload(IMAGE_KEY, file);
        return HttpResponse.seeOther(URI.create(REDIRECT_PATH)); // <8>
    }
    //end::upload[]

    //tag::delete[]
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED) // <5>
    @Post("/delete") // <6>
    public HttpResponse delete(String key) {
        fileRepository.delete(key);
        return HttpResponse.seeOther(URI.create(REDIRECT_PATH)); // <7>
    }
    //end::delete[]

    //tag::model[]
    private Map<String, Object> model(@Nullable String key) {
        Map<String, Object> model = new HashMap<>();
        if (key != null) {
            if (fileRepository.doesObjectExists(key)) {
                model.put("imageurl", fileRepository.findURLbyKey(key).toString());
            }
            model.put("key", key);
        }
        return model;
    }
    //end::model[]
}
