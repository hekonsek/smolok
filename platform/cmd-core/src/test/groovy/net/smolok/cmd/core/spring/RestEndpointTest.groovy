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
import org.apache.commons.io.IOUtils
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
import smolok.lib.common.Networks
import smolok.lib.docker.Docker

import static com.google.common.io.Files.createTempDir
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Networks.findAvailableTcpPort
import static smolok.lib.common.Uuids.uuid
import static smolok.lib.docker.ContainerStatus.created
import static smolok.lib.docker.ContainerStatus.running
import static smolok.lib.process.ExecutorBasedProcessManager.command
import static smolok.status.handlers.eventbus.EventBusMetricHandler.EVENTBUS_CAN_SEND_METRIC_KEY

@RunWith(SpringRunner.class)
@SpringBootTest(classes = [RestEndpointTest.class, KapuaApplication.class])
@Configuration
class RestEndpointTest {

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

    static restPort = findAvailableTcpPort()

    @BeforeClass
    static void beforeClass() {
        System.setProperty('agent.rest.port', "${restPort}")
    }

    // Cloud tests

    @Test
    void shouldReturnWelcomeMessage() {
        // When
        def commandId = IOUtils.toString(new URL("http://localhost:${restPort}/execute/" + new String(Base64.encoder.encode('--help'.getBytes()))))
        def output = IOUtils.toString(new URL("http://localhost:${restPort}/output/${commandId}/0"))
        def outputList = output.split('\n')
        assertThat(outputList[1]).startsWith('Welcome to Smolok')
    }

    @Test
    void shouldReturnExecutionDoneMarker() {
        // When
        def commandId = IOUtils.toString(new URL("http://localhost:${restPort}/execute/" + new String(Base64.encoder.encode('--help'.getBytes()))))

        def output = IOUtils.toString(new URL("http://localhost:${restPort}/output/${commandId}/0"))
        def outputList = output.split('\n')

        output = IOUtils.toString(new URL("http://localhost:${restPort}/output/${commandId}/${outputList[0]}"))
        outputList = output.split('\n')

        assertThat(outputList[0]).isEqualTo('-1')
    }

}