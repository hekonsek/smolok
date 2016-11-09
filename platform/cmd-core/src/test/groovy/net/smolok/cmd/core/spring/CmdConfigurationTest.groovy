/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.smolok.cmd.core.spring

import net.smolok.cmd.core.Command
import net.smolok.cmd.core.CommandDispatcher
import net.smolok.cmd.core.OutputSink
import net.smolok.cmd.core.TestCommand
import net.smolok.paas.Paas
import org.apache.camel.builder.RouteBuilder
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.junit4.SpringRunner
import smolok.lib.docker.Docker

import static com.google.common.io.Files.createTempDir
import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.MINUTES
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Awaitilities.condition
import static smolok.lib.common.Networks.findAvailableTcpPort
import static smolok.lib.common.Uuids.uuid
import static smolok.lib.docker.ContainerStatus.created
import static smolok.lib.docker.ContainerStatus.running
import static smolok.lib.process.ExecutorBasedProcessManager.command
import static smolok.status.handlers.eventbus.EventBusMetricHandler.EVENTBUS_CAN_SEND_METRIC_KEY

@RunWith(SpringRunner.class)
@SpringBootTest(classes = [CmdConfigurationTest.class, KapuaApplication.class])
@Configuration
class CmdConfigurationTest {

    def commandId = uuid()

    @Bean
    Command testCommand() {
        new TestCommand()
    }

    @Bean
    RouteBuilder routeBuilder() {
        new RouteBuilder() {
            @Override
            void configure() {
                from('direct:echo').log('Echo!')
            }
        }
    }

    @Autowired
    OutputSink outputSink

    @Autowired
    CommandDispatcher commandHandler

    // Raspbian install fixtures

    static def devicesDirectory = createTempDir()

    @BeforeClass
    static void beforeClass() {
        System.setProperty('raspbian.image.uri', 'https://repo1.maven.org/maven2/com/google/guava/guava/19.0/guava-19.0.jar')
        System.setProperty('devices.directory', devicesDirectory.absolutePath)
        System.setProperty('raspbian.image.file.name.extracted', uuid())
        System.setProperty('raspbian.image.file.name.compressed', "${uuid()}.zip")
        System.setProperty('agent.rest.port', "${findAvailableTcpPort()}")
    }

    // PaaS fixtures

    @Autowired
    Paas paas

    @Before
    void before() {
        paas.reset()
    }

    // Spark fixtures

    @Autowired
    Docker docker

    // Cloud tests

    @Test
    void shouldExecuteCloudStartCommand() {
        // When
        def commandId = commandHandler.handleCommand('cloud', 'start')

        // Then
        assertThat(paas.started)
    }

    @Test
    void shouldInformAboutCloudStart() {
        // When
        def commandId = commandHandler.handleCommand('cloud', 'start')

        // Then
        await().atMost(3, MINUTES).until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0)).hasSize(2)
        assertThat(outputSink.output(commandId, 0)).containsSubsequence('Smolok Cloud started.')
    }

    @Test
    void shouldShowEventBusStatus() {
        // Given
        paas.start()

        // When
        def commandId = commandHandler.handleCommand('cloud', 'status')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        def eventBusStatus = outputSink.output(commandId, 0).find{ it.startsWith(EVENTBUS_CAN_SEND_METRIC_KEY) }
        assertThat(eventBusStatus).startsWith("${EVENTBUS_CAN_SEND_METRIC_KEY}\t${true}")
    }

    @Test
    void cloudResetShouldNotAcceptOptions() {
        // When
        def commandId = commandHandler.handleCommand('cloud', 'reset', '--someOption')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).first()).contains('Unsupported options used')
    }

    // "smolok sdcard install-raspbian" tests

    @Test
    void shouldInstallImageOnDevice() {
        // Given
        def device = new File(devicesDirectory, 'foo')
        device.createNewFile()

        // When
        def commandId = commandHandler.handleCommand('sdcard', 'install-raspbian', 'foo')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(device.length()).isGreaterThan(0L)
    }

    @Test
    void shouldValidateDeviceParameterAbsence() {
        // When
        def commandId = commandHandler.handleCommand('sdcard', 'install-raspbian')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).first()).startsWith('Device not specified.')
    }

    @Test
    void shouldValidateDeviceAbsence() {
        // When
        def commandId = commandHandler.handleCommand('sdcard', 'install-raspbian', 'bar')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).first()).matches('Device .* does not exist.')
    }

    // Spark tests

    @Test
    void shouldStartSpark() {
        // When
        def commandId = commandHandler.handleCommand(command('spark start'))

        // Then
        assertThat(docker.status('spark-master')).isIn(created, running)
        assertThat(docker.status('spark-worker')).isIn(created, running)
    }

    @Test
    void shouldValidateInvalidSparkCluster() {
        // When
        def commandId = commandHandler.handleCommand(command('spark start foo'))

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).first()).isEqualTo('Unknown Spark node type: foo')
    }

    @Test
    void shouldHandleEmptyCommand() {
        // When
        def commandId = commandHandler.handleCommand()

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).first()).matches(/Cannot execute empty command.+/)
    }

    // Help tests

    @Test
    void shouldDisplayGlobalHelp() {
        // When
        def commandId = commandHandler.handleCommand('--help')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).first()).startsWith('Welcome')
    }

    @Test
    void shouldDisplayCommandHelp() {
        // When
        def commandId = commandHandler.handleCommand('this', 'is', 'my', 'command', '--help')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).first().split(/\n/).toList()).hasSize(3)
    }

    // Endpoint command tests

    @Test
    void shouldSendMessageToEndpoint() {
        // When
        def commandId = commandHandler.handleCommand('endpoint', 'direct:echo', '[foo: "bar"]')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).first()).isEqualTo('{foo=bar}')
    }

    // cloud service-start test

    @Test
    void shouldStartDeviceService() {
        // Given
        paas.start()

        // When
        def commandId = commandHandler.handleCommand('service-start', 'device')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).last()).containsIgnoringCase('started')
    }

    @Test
    void shouldStartRestAdapter() {
        // Given
        paas.start()

        // When
        def commandId = commandHandler.handleCommand('adapter-start', 'rest')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).last()).containsIgnoringCase('started')
    }

    @Test
    void shouldNotStartInvalidServiceLocator() {
        // Given
        paas.start()

        // When
        def commandId = commandHandler.handleCommand('service-start', 'invalidCommand')

        // Then
        await().atMost(2, MINUTES).until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0)[1]).containsIgnoringCase('problem starting service container')
    }

    @Test
    void shouldStartZeppelin() {
        // When
        def commandId = commandHandler.handleCommand(command('zeppelin start'))

        // Then
        assertThat(docker.status('zeppelin')).isIn(created, running)
    }

    @Test
    @IfProfileValue(name = 'test-profile', value = 'docker')
    @Ignore('Need to resolve issue of building docker images on travis')
    void sparkSubmitShouldReturnValidErrorMessageOnStart() {
        // When
        def commandId = commandHandler.handleCommand(command('spark submit'))

        // Then
        assertThat(outputSink.output(commandId, 0)[0]).doesNotContain('exec format error')
        assertThat(outputSink.output(commandId, 0)[0]).containsIgnoringCase('Error: Cannot load main class from JAR file')
    }
}