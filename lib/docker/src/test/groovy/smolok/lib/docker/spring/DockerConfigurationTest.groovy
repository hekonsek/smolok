package smolok.lib.docker.spring

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.bootstrap.Smolok
import smolok.lib.docker.Container
import smolok.lib.docker.Docker

import static java.util.UUID.randomUUID
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.docker.Container.container

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Smolok.class)
class DockerConfigurationTest {

    @Autowired
    Docker docker


    @Test
    void shouldCreateContainer() {
        def status = docker.createAndStart(container('ubuntu', randomUUID().toString()))
        assertThat(status).isEqualTo(Docker.ContainerStartupStatus.created)
    }

    @Test
    void shouldNotStartContainerSecondTime() {
        def containername = randomUUID().toString()
        docker.createAndStart(new Container('ubuntu', containername, 'host'))
        def status = docker.createAndStart(new Container('ubuntu', containername, 'host'))
        assertThat(status).isEqualTo(Docker.ContainerStartupStatus.alreadyRunning)
    }

}
