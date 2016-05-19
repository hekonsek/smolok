package smolok.status.spring

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.bootstrap.Smolok
import smolok.lib.process.ProcessManager
import smolok.paas.Paas
import smolok.status.StatusResolver

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.process.ExecutorBasedProcessManager.command

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = [Smolok.class, StatusResolverConfigurationTest.class])
class StatusResolverConfigurationTest {

    // Tests subject

    @Autowired
    StatusResolver statusResolver

    // Collaborators

    @Autowired
    Paas paas

    @Autowired
    ProcessManager processManager

    // Fixtures

    @Before
    void before() {
        paas.reset()
        paas.start()
    }

    // Tests

    @Test
    void canSendToEventBus() {
        // When
        def canSendToEventBus = statusResolver.status().find{ it.key == 'eventbus.canSend' }

        // Then
        assertThat(canSendToEventBus).isNotNull()
        assertThat(canSendToEventBus.value).isEqualTo("true")
        assertThat(canSendToEventBus.warning).isEqualTo(false)
    }

    @Test
    void cannotSendToEventBus() {
        // Given
        def eventsBusPid = processManager.execute(command('docker ps')).find{ it.contains('k8s_eventbus') }.replaceFirst(/\s.+/, '')
        processManager.execute(command("docker stop ${eventsBusPid}"))

        // When
        def canSendToEventBus = statusResolver.status().find{ it.key == 'eventbus.canSend' }

        // Then
        assertThat(canSendToEventBus).isNotNull()
        assertThat(canSendToEventBus.value).isEqualTo("false")
        assertThat(canSendToEventBus.warning).isEqualTo(true)
    }

}
