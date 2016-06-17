package smolok.eventbus.client.spring

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.EqualsAndHashCode
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
import smolok.eventbus.client.EventBus

import static java.util.UUID.randomUUID
import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort
import static smolok.eventbus.client.Header.arguments

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Smolok.class)
@Configuration
class EventBusClientConfigurationTest {

    @Autowired
    EventBus eventBus

    @BeforeClass
    static void beforeClass() {
        System.setProperty('amqp.port', "${findAvailableTcpPort()}")
    }

    @Bean
    RouteBuilder routeBuilder() {
        new RouteBuilder() {
            @Override
            void configure() throws Exception {
                from('amqp:echo').log('echo')

                from('amqp:echoArgument').setBody().header('SMOLOK_ARG0')

                from('amqp:pojoResponse').setBody().constant(new ObjectMapper().writeValueAsBytes(new Response(body: 'foo')))
            }
        }
    }

    def payload = randomUUID().toString()

    // Tests

    @Test
    void shouldReceiveResponse() {
        // When
        def response = eventBus.fromBus('echo', payload, String.class)

        // Then
        assertThat(response).isEqualTo(payload)
    }

    @Test
    void shouldReceiveSmolokArgument() {
        // When
        def response = eventBus.fromBus('echoArgument', null, String.class, arguments('foo'))

        // Then
        assertThat(response).isEqualTo('foo')
    }

    @Test
    void shouldReceivePojoResponse() {
        // When
        def response = eventBus.fromBus('pojoResponse', payload, Response.class)

        // Then
        assertThat(response).isEqualTo(new Response(body: 'foo'))
    }

    // Test classes

    @EqualsAndHashCode
    static class Response {

        String body

    }

}