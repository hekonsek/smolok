package smolok.cmd.spring

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.bootstrap.Smolok
import smolok.cmd.CommandDispatcher
import smolok.cmd.InMemoryOutputSink
import smolok.paas.Paas

import static org.assertj.core.api.Assertions.assertThat
import static smolok.status.handlers.eventbus.EventBusMetricHandler.EVENTBUS_CAN_SEND_METRIC_KEY

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Smolok.class)
@Configuration
class CmdConfigurationTest {

    @Bean
    InMemoryOutputSink outputSink() {
        new InMemoryOutputSink()
    }

    @Autowired
    InMemoryOutputSink outputSink

    @Autowired
    CommandDispatcher commandHandler

    @Autowired
    Paas paas

    @Before
    void before() {
        outputSink.reset()
        paas.reset()
    }

    // Tests

    @Test
    void shouldExecuteCloudStartCommand() {
        // When
        commandHandler.handleCommand('cloud', 'start')

        // Then
        assertThat(paas.started)
    }

    @Test
    void shouldInformAboutCloudStart() {
        // When
        commandHandler.handleCommand('cloud', 'start')

        // Then
        assertThat(outputSink.output()).hasSize(2)
        assertThat(outputSink.output()).containsSubsequence('Smolok Cloud started.')
    }

    @Test
    void shouldShowEventBusStatus() {
        // Given
        paas.start()

        // When
        commandHandler.handleCommand('cloud', 'status')

        // Then
        def eventBusStatus = outputSink.output().find{ it.startsWith(EVENTBUS_CAN_SEND_METRIC_KEY) }
        assertThat(eventBusStatus).startsWith("${EVENTBUS_CAN_SEND_METRIC_KEY}\t${true}")
    }


}