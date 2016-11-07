package smolok.eventbus.client.spring

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.EqualsAndHashCode
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import smolok.eventbus.client.EventBus

import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort
import static smolok.eventbus.client.Header.arguments
import static smolok.eventbus.client.Header.smolokHeaderKey
import static smolok.lib.common.Uuids.uuid

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KapuaApplication.class)
@Configuration
class EventBusClientConfigurationTest {

    @Autowired
    EventBus eventBus

    @Autowired
    ProducerTemplate producerTemplate

    @BeforeClass
    static void beforeClass() {
        System.setProperty('EVENTBUS_SERVICE_PORT', "${findAvailableTcpPort()}")
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

    def payload = uuid()

    // Tests

    @Test
    void shouldSendPojoRequest() {
        // When
        def response = eventBus.fromBus('echo', new Request(payload: payload), Request.class)

        // Then
        assertThat(response).isEqualTo(new Request(payload: payload))
    }

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

    // Camel component tests

    @Test
    void shouldReceiveResponseFromCamelComponent() {
        // When
        def response = producerTemplate.requestBody('eventbus:echo', payload, String.class)

        // Then
        assertThat(response).isEqualTo(payload)
    }

    @Test
    void shouldReceiveSmolokArgumentFromCamel() {
        // When
        def response = producerTemplate.requestBodyAndHeader('eventbus:echoArgument', payload, smolokHeaderKey(0), 'foo', String.class)

        // Then
        assertThat(response).isEqualTo('foo')
    }

    @Test
    void shouldNotReceiveSmolokArgumentFromCamel() {
        // When
        def response = producerTemplate.requestBodyAndHeader('eventbus:echoArgument', payload, smolokHeaderKey(1000), 'foo', String.class)

        // Then
        assertThat(response).isNull()
    }

    // Test classes

    @EqualsAndHashCode
    static class Request {

        String payload

    }

    @EqualsAndHashCode
    static class Response {

        String body

    }

}