package smolok.cmd.spring

import com.google.common.io.Files
import org.junit.Before
import org.junit.BeforeClass
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

import static com.google.common.io.Files.createTempDir
import static java.util.UUID.randomUUID
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

    // Raspbian install fixtures

    static def devicesDirectory = createTempDir()

    @BeforeClass
    static void beforeClass() {
        System.setProperty('raspbian.image.uri', 'https://repo1.maven.org/maven2/com/google/guava/guava/19.0/guava-19.0.jar')
        System.setProperty('devices.directory', devicesDirectory.absolutePath)
        System.setProperty('raspbian.image.file.name.extracted', randomUUID().toString())
        System.setProperty('raspbian.image.file.name.compressed', randomUUID().toString())
    }

    // PaaS fixtures

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

    // "smolok sdcard install-raspbian" tests

    @Test
    void shouldInstallImageOnDevice() {
        // When
        commandHandler.handleCommand('sdcard', 'install-raspbian', 'foo')

        // Then
        assertThat(new File(devicesDirectory, 'foo').length()).isGreaterThan(0L)
    }

    @Test
    void shouldValidateDeviceAbsence() {
        // When
        commandHandler.handleCommand('sdcard', 'install-raspbian')

        // Then
        assertThat(outputSink.output().first()).startsWith('Device not specified.')
    }


}