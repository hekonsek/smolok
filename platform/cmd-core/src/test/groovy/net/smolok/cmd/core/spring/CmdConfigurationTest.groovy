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

import org.apache.camel.builder.RouteBuilder
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.junit4.SpringRunner
import smolok.bootstrap.Smolok
import net.smolok.cmd.core.Command
import net.smolok.cmd.core.CommandDispatcher
import net.smolok.cmd.core.InMemoryOutputSink
import net.smolok.cmd.core.TestCommand
import smolok.lib.docker.Docker
import net.smolok.paas.Paas

import static com.google.common.io.Files.createTempDir
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Uuids.uuid
import static smolok.lib.docker.ContainerStatus.created
import static smolok.lib.docker.ContainerStatus.running
import static smolok.lib.process.ExecutorBasedProcessManager.command
import static smolok.status.handlers.eventbus.EventBusMetricHandler.EVENTBUS_CAN_SEND_METRIC_KEY

@RunWith(SpringRunner.class)
@SpringBootTest(classes = [CmdConfigurationTest.class, Smolok.class])
@Configuration
class CmdConfigurationTest {

    @Bean
    InMemoryOutputSink outputSink() {
        new InMemoryOutputSink()
    }

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
    InMemoryOutputSink outputSink

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

    // Cloud tests

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

    @Test
    void cloudResetShouldNotAcceptOptions() {
        // When
        commandHandler.handleCommand('cloud', 'reset', '--someOption')

        // Then
        assertThat(outputSink.output().first()).contains('Unsupported options used')
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

    @Test
    void shouldHandleEmptyCommand() {
        // When
        commandHandler.handleCommand()

        // Then
        assertThat(outputSink.output().first()).matches(/Cannot execute empty command.+/)
    }

    // Help tests

    @Test
    void shouldDisplayGlobalHelp() {
        // When
        commandHandler.handleCommand('--help')

        // Then
        assertThat(outputSink.output().first()).startsWith('Welcome')
    }

    @Test
    void shouldDisplayCommandHelp() {
        // When
        commandHandler.handleCommand('this', 'is', 'my', 'command', '--help')

        // Then
        assertThat(outputSink.output().first().split(/\n/).toList()).hasSize(3)
    }

    // Endpoint command tests

    @Test
    void shouldSendMessageToEndpoint() {
        // When
        commandHandler.handleCommand('endpoint', 'direct:echo', '[foo: "bar"]')

        // Then
        assertThat(outputSink.output().first()).isEqualTo('{foo=bar}')
    }

    // cloud service-start test

    @Test
    void shouldStartDeviceService() {
        // Given
        paas.start()

        // When
        commandHandler.handleCommand('service-start', 'device')

        // Then
        assertThat(outputSink.output().last()).containsIgnoringCase('started')
    }

    @Test
    void shouldStartRestAdapter() {
        // Given
        paas.start()

        // When
        commandHandler.handleCommand('adapter-start', 'rest')

        // Then
        assertThat(outputSink.output().last()).containsIgnoringCase('started')
    }

    @Test
    void shouldNotStartInvalidServiceLocator() {
        // Given
        paas.start()

        // When
        commandHandler.handleCommand('service-start', 'invalidCommand')

        // Then
        assertThat(outputSink.output()[1]).containsIgnoringCase('problem starting service container')
    }

    @Test
    void shouldStartZeppelin() {
        // When
        commandHandler.handleCommand(command('zeppelin start'))

        // Then
        assertThat(docker.status('zeppelin')).isIn(created, running)
    }

    @Test
    @IfProfileValue(name = 'test-profile', value = 'docker')
    void sparkSubmitShouldReturnValidErrorMessageOnStart() {
        // When
        commandHandler.handleCommand(command('spark submit'))

        // Then
        assertThat(outputSink.output()[0]).doesNotContain('exec format error')
        assertThat(outputSink.output()[0]).containsIgnoringCase('Error: Cannot load main class from JAR file')
    }
}