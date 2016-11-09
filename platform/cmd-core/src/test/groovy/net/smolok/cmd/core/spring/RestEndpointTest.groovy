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

import org.apache.commons.io.IOUtils
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static com.jayway.awaitility.Awaitility.await
import static java.util.Base64.encoder
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Awaitilities.condition
import static smolok.lib.common.Networks.findAvailableTcpPort

@RunWith(SpringRunner)
@SpringBootTest(classes = [RestEndpointTest, KapuaApplication])
class RestEndpointTest {

    static restPort = findAvailableTcpPort()

    @BeforeClass
    static void beforeClass() {
        System.setProperty('agent.rest.port', "${restPort}")
    }

    def executeUrl = "http://localhost:${restPort}/execute"

    def outputUrl = { commandId, offset -> new URL("http://localhost:${restPort}/output/${commandId}/${offset}") }

    def encodedHelpOption = new String(encoder.encode('--help'.getBytes()))

    def executeHelpUrl = new URL("${executeUrl}/${encodedHelpOption}")

    // Tests

    @Test
    void shouldReturnWelcomeMessage() {
        // Given
        def commandId = IOUtils.toString(executeHelpUrl)
        await().until condition { IOUtils.toString(outputUrl(commandId, 0)) != '0' }

        // When
        def output = IOUtils.toString(outputUrl(commandId, 0))
        def outputList = output.split('\n')

        // Then
        assertThat(outputList[1]).startsWith('Welcome to Smolok')
    }

    @Test
    void shouldReturnExecutionDoneMarker() {
        // When
        def commandId = IOUtils.toString(executeHelpUrl)
        await().until condition { IOUtils.toString(new URL("http://localhost:${restPort}/output/${commandId}/0") ) != '0' }

        def output = IOUtils.toString(new URL("http://localhost:${restPort}/output/${commandId}/0"))
        def outputList = output.split('\n')

        output = IOUtils.toString(new URL("http://localhost:${restPort}/output/${commandId}/${outputList[0]}"))
        outputList = output.split('\n')

        assertThat(outputList[0]).isEqualTo('-1')
    }

}