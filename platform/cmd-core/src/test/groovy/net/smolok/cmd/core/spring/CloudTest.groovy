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
import net.smolok.cmd.core.OutputSink
import net.smolok.paas.Paas
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static com.google.common.io.Files.createTempDir
import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.MINUTES
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Awaitilities.condition
import static smolok.lib.common.Networks.findAvailableTcpPort
import static smolok.lib.common.Uuids.uuid
import static smolok.status.handlers.eventbus.EventBusMetricHandler.EVENTBUS_CAN_SEND_METRIC_KEY

@RunWith(SpringRunner)
@SpringBootTest(classes = KapuaApplication)
class CloudTest {

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

}