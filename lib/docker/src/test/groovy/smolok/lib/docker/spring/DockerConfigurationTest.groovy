package smolok.lib.docker.spring

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.bootstrap.Smolok
import smolok.lib.docker.Container
import smolok.lib.docker.ContainerBuilder
import smolok.lib.docker.ContainerStatus
import smolok.lib.docker.Docker

import static java.util.UUID.randomUUID
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.docker.Container.container
import static smolok.lib.docker.ServiceStartupStatus.alreadyRunning
import static smolok.lib.docker.ServiceStartupStatus.created

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Smolok.class)
class DockerConfigurationTest {

    @Autowired
    Docker docker

    def containerName = randomUUID().toString()

    // Tests

    @Test
    void shouldCreateContainer() {
        def status = docker.startService(container('ubuntu', containerName))
        assertThat(status).isEqualTo(created)
    }

    @Test
    void shouldStartCreatedContainer() {
        def startupStatus = docker.startService(new ContainerBuilder('ubuntu').name(containerName).net('host').arguments('top').build())
        def containerStatus = docker.status(containerName)
        assertThat(startupStatus).isEqualTo(created)
        assertThat(containerStatus).isEqualTo(ContainerStatus.running)
    }

    @Test
    void shouldNotStartContainerSecondTime() {
        docker.startService(new Container('ubuntu', containerName, 'host', [:], [:]))
        def status = docker.startService(new Container('ubuntu', containerName, 'host', [:], [:]))
        assertThat(status).isEqualTo(alreadyRunning)
    }

    @Test
    void shouldStopContainer() {
        // Given
        docker.startService(new ContainerBuilder('ubuntu').name(containerName).net('host').arguments('top').build())

        // When
        docker.stop(containerName)

        // Then
        def containerStatus = docker.status(containerName)
        assertThat(containerStatus).isEqualTo(ContainerStatus.created)
    }

}
