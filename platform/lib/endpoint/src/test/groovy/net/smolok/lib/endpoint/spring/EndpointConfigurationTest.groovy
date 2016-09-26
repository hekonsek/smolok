package net.smolok.lib.endpoint.spring

import net.smolok.lib.endpoint.Endpoint
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.spring.boot.CamelAutoConfiguration
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner

import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringRunner)
@SpringBootTest(classes = [CamelAutoConfiguration, EndpointConfiguration, EndpointConfigurationTest])
class EndpointConfigurationTest {

    @Autowired
    Endpoint endpoint

    String echoEndpoint = 'direct:echo'

    @Bean
    RouteBuilder routeBuilder() {
        new RouteBuilder() {
            @Override
            void configure() {
                from(echoEndpoint).log('Echo!')
            }
        }
    }

    // Tests

    @Test
    void shouldSendMap() {
        def response = endpoint.request(echoEndpoint, '[foo: "bar"]')
        assertThat(response).isEqualTo('{foo=bar}')
    }

    @Test
    void shouldSendPrefixedGroovyBody() {
        def response = endpoint.request(echoEndpoint, 'groovy:"foo"')
        assertThat(response).isEqualTo('foo')
    }

    @Test
    void shouldSendGroovyPayload() {
        def response = endpoint.request(echoEndpoint, 'groovy:2+2', 'groovy')
        assertThat(response).isEqualTo('4')
    }

    // Response tests

    @Test
    void shouldResponseWithStringByDefault() {
        def response = endpoint.request(echoEndpoint, '"foo"')
        assertThat(response).isEqualTo('foo')
    }

    @Test
    void shouldExchangeNull() {
        def response = endpoint.request(echoEndpoint, 'null')
        assertThat(response).isEqualTo('null')
    }

}
