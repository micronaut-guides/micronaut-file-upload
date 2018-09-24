package example.micronaut

import geb.spock.GebSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Shared

class HomeSpec extends GebSpec {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

    def "upload #imagename to S3 and delete"(String imagename) {
        given:
        browser.baseUrl = "http://localhost:${embeddedServer.port}"
        File f = new File('src/test/resources/micronaut_mini_copy_tm.svg')

        expect:
        f.exists()

        when:
        embeddedServer.applicationContext.getBean(AwsCredentialsConfigurationProperties)

        then:
        noExceptionThrown()

        when:
        embeddedServer.applicationContext.getBean(S3FileRepository)

        then:
        noExceptionThrown()

        when:
        to HomePage

        then:
        at HomePage
        !hasImage()

        when:
        uploadFile(f.absolutePath)

        then:
        at HomePage
        hasImage()

        when:
        delete()

        then:
        at HomePage
        !hasImage()

        where:
        imagename << ['micronaut_mini_copy_tm.svg', 'blacklogo.png']
    }
}
