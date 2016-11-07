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
package smolok.eventbus.spring

import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner

import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort
import static smolok.lib.common.Properties.setIntProperty

@RunWith(SpringRunner)
@SpringBootTest(classes = [KapuaApplication, EventBusConfigurationTest])
class EventBusConfigurationTest {

    // Collaborators

    @Autowired
    ProducerTemplate producerTemplate

    // Fixtures

    @BeforeClass
    static void beforeClass() {
        setIntProperty('EVENTBUS_SERVICE_PORT', findAvailableTcpPort())
    }

    @Bean
    RouteBuilder amqpEchoRoute() {
        new RouteBuilder() {
            void configure() {
                from('amqp:echo').log('Calling echo service.')
            }
        }
    }

    // Tests

    @Test
    void shouldPerformRequestReplyWithAmqp() {
        // Given
        def msg = 'hello!'

        // When
        def response = producerTemplate.requestBody('amqp:echo', msg, String.class)

        // Then
        assertThat(response).isEqualTo(msg)
    }

}