package example.micronaut

import geb.spock.GebSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.IgnoreIf
import spock.lang.Shared
import spock.lang.Unroll

class HomeSpec extends GebSpec {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

    @IgnoreIf({ !env['AWS_S3_REGION'] || !env['AWS_SECRET_KEY'] || !env['AWS_S3_BUCKET'] || !env['AWS_ACCESS_KEY_ID']})
    @Unroll
    def "upload #imagename to S3 and delete"(String imagename) {
        given:
        browser.baseUrl = "http://localhost:${embeddedServer.port}"
        File f = new File("src/test/resources/$imagename")

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
        sleep(2_000) // sleep for two seconds to render image
        delete()

        then:
        at HomePage
        !hasImage()

        where:
        imagename << ['blacklogo.png']
    }
}
