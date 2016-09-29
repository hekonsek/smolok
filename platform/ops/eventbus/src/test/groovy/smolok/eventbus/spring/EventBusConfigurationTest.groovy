package smolok.eventbus.spring

import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.bootstrap.Smolok

import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Smolok.class)
@Configuration
class EventBusConfigurationTest {

    @Autowired
    ProducerTemplate producerTemplate

    // Fixtures

    @BeforeClass
    static void beforeClass() {
        System.setProperty('EVENTBUS_SERVICE_PORT', "${findAvailableTcpPort()}")
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