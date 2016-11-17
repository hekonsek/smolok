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

import net.smolok.cmd.core.BaseCommandHandler
import net.smolok.cmd.spi.CommandHandler
import net.smolok.cmd.spi.OutputSink
import org.apache.commons.io.IOUtils
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringRunner

import java.util.concurrent.TimeUnit

import static com.jayway.awaitility.Awaitility.await
import static java.util.Base64.encoder
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Awaitilities.condition
import static smolok.lib.common.Networks.findAvailableTcpPort

@RunWith(SpringRunner)
@SpringBootTest(classes = [RestEndpointTest, KapuaApplication])
@Configuration
class RestEndpointTest {

    static restPort = findAvailableTcpPort()

    @BeforeClass
    static void beforeClass() {
        System.setProperty('agent.rest.port', "${restPort}")
    }

    def baseUrl = "http://localhost:${restPort}"

    def executeUrl = { commandId -> new URL("${baseUrl}/execute/${commandId}") }

    def outputUrl = { commandId, offset -> new URL("${baseUrl}/output/${commandId}/${offset}") }

    def encodedHelpOption = new String(encoder.encode('--help'.getBytes()))

    def executeHelpUrl = executeUrl(encodedHelpOption)

    @Bean
    CommandHandler slowCommand() {
        new SlowCommandHandler()
    }

    static class SlowCommandHandler extends BaseCommandHandler {

        SlowCommandHandler() {
            super(['slowCommand'])
        }

        @Override
        void handle(OutputSink outputSink, String commandId, String... command) {
            TimeUnit.MINUTES.sleep(5)
        }

    }

    // Tests

    @Test
    void shouldReturnWelcomeMessage() {
        // Given
        def commandId = IOUtils.toString(executeHelpUrl)
        await().until condition { IOUtils.toString(outputUrl(commandId, 0)) != '0' }

        // When
        def output = IOUtils.toString(outputUrl(commandId, 0))
        def outputList = output.split('___')

        // Then
        assertThat(outputList[1]).startsWith('Welcome to Smolok')
    }

    @Test
    void shouldReturnExecutionDoneMarker() {
        // Given
        def commandId = IOUtils.toString(executeHelpUrl)
        await().until condition { IOUtils.toString(outputUrl(commandId, 0)) != '0' }
        def output = IOUtils.toString(outputUrl(commandId, 0))
        def outputParts = output.split('___')

        // When
        output = IOUtils.toString(outputUrl(commandId, outputParts[0].toInteger()))
        outputParts = output.split('___')

        // Then
        assertThat(outputParts[0]).isEqualTo('-1')
    }

    @Test
    void shouldReturnEmptyOffsetForNoOutput() {
        // Given
        def commandId = IOUtils.toString(executeUrl(new String(encoder.encode('slowCommand'.getBytes()))))

        // When
        def output = IOUtils.toString(outputUrl(commandId, 0))

        // Then
        assertThat(output).isEqualTo('0')
    }

}