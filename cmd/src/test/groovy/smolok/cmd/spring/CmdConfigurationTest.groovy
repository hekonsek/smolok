package smolok.cmd.spring

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.bootstrap.Smolok
import smolok.cmd.CommandDispatcher
import smolok.cmd.InMemoryOutputSink
import smolok.paas.Paas

import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = [Smolok.class, CmdConfigurationTest.class])
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

}