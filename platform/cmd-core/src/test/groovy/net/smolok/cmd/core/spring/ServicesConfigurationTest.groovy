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

import net.smolok.cmd.core.CommandDispatcher
import net.smolok.cmd.spi.OutputSink
import net.smolok.paas.Paas
import net.smolok.service.configuration.api.ConfigurationService
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringRunner

import static com.google.common.io.Files.createTempDir
import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.MINUTES
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Awaitilities.condition
import static smolok.lib.common.Networks.findAvailableTcpPort
import static smolok.lib.common.Uuids.uuid

@RunWith(SpringRunner.class)
@SpringBootTest(classes = [KapuaApplication.class])
@Configuration
class ServicesConfigurationTest {

    @Autowired
    OutputSink outputSink

    @Autowired
    CommandDispatcher commandDispatcher

    @Autowired
    ConfigurationService configurationService

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

    // cloud service-start test

    @Test
    void shouldStartDeviceService() {
        // Given
        paas.start()

        // When
        def commandId = commandDispatcher.handleCommand('service-start', 'device')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).last()).containsIgnoringCase('started')
    }

    @Test
    void shouldRegisterDeviceService() {
        // Given
        paas.start()

        // When
        def commandId = commandDispatcher.handleCommand('service-start', 'device')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        def isDeviceServiceRegistered = configurationService.get('service.directory.device').toBoolean()
        assertThat(isDeviceServiceRegistered).isTrue()
    }

    @Test
    void shouldStartRestAdapter() {
        // Given
        paas.start()

        // When
        def commandId = commandDispatcher.handleCommand('adapter-start', 'rest')

        // Then
        await().until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0).last()).containsIgnoringCase('started')
    }

    @Test
    void shouldNotStartInvalidServiceLocator() {
        // Given
        paas.start()

        // When
        def commandId = commandDispatcher.handleCommand('service-start', 'invalidCommand')

        // Then
        await().atMost(2, MINUTES).until condition {outputSink.isDone(commandId)}
        assertThat(outputSink.output(commandId, 0)[1]).containsIgnoringCase('problem starting service container')
    }

}