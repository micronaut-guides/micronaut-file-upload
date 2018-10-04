package example.micronaut

import geb.spock.GebSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.IgnoreIf
import spock.lang.Shared
import spock.util.concurrent.PollingConditions

class CreatePageSpec extends GebSpec {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

    @IgnoreIf({ !env['AWS_S3_REGION'] || !env['AWS_SECRET_KEY'] || !env['AWS_S3_BUCKET'] || !env['AWS_ACCESS_KEY_ID']})
    def "upload image to S3 and delete"() {
        given:
        final String imagename = 'blacklogo.png'
        browser.baseUrl = "http://localhost:${embeddedServer.port}"
        File f = new File("src/test/resources/$imagename")

        expect:
        f.exists()

        when:
        FileRepository fileRepository = embeddedServer.applicationContext.getBean(FileRepository)

        then:
        noExceptionThrown()

        when:
        to CreatePage

        then:
        at CreatePage

        when:
        CreatePage createPage = browser.page(CreatePage)

        then:
        !createPage.hasImage()

        when:
        createPage.uploadFile(f.absolutePath)

        then:
        at CreatePage

        when:
        PollingConditions conditions = new PollingConditions(timeout: 5)

        then:
        conditions.eventually {
            fileRepository.doesObjectExists(StreamingFileUploadController.IMAGE_KEY)
        }

        cleanup:
        if(fileRepository.doesObjectExists(StreamingFileUploadController.IMAGE_KEY)) {
            fileRepository.delete(StreamingFileUploadController.IMAGE_KEY)
        }

    }
}
