package smolok.cmd.spring

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
import smolok.lib.docker.Docker
import smolok.paas.Paas

import static com.google.common.io.Files.createTempDir
import static java.util.UUID.randomUUID
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.docker.ContainerStatus.created
import static smolok.lib.docker.ContainerStatus.running
import static smolok.lib.process.ExecutorBasedProcessManager.command
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

    // Spark fixtures

    @Autowired
    Docker docker

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
        // Given
        def device = new File(devicesDirectory, 'foo')
        device.createNewFile()

        // When
        commandHandler.handleCommand('sdcard', 'install-raspbian', 'foo')

        // Then
        assertThat(device.length()).isGreaterThan(0L)
    }

    @Test
    void shouldValidateDeviceParameterAbsence() {
        // When
        commandHandler.handleCommand('sdcard', 'install-raspbian')

        // Then
        assertThat(outputSink.output().first()).startsWith('Device not specified.')
    }

    @Test
    void shouldValidateDeviceAbsence() {
        // When
        commandHandler.handleCommand('sdcard', 'install-raspbian', 'bar')

        // Then
        assertThat(outputSink.output().first()).matches('Device .* does not exist.')
    }

    // Spark tests

    @Test
    void shouldStartSpark() {
        // When
        commandHandler.handleCommand(command('spark start'))

        // Then
        assertThat(docker.status('spark-master')).isIn(created, running)
        assertThat(docker.status('spark-worker')).isIn(created, running)
    }

    @Test
    void shouldValidateInvalidSparkCluster() {
        // When
        commandHandler.handleCommand(command('spark start foo'))

        // Then
        assertThat(outputSink.output().first()).isEqualTo('Unknown Spark node type: foo')
    }

}