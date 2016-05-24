package smolok.eventbus.client.spring

import org.apache.camel.builder.RouteBuilder
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.bootstrap.Smolok
import smolok.eventbus.client.EventBus
import smolok.paas.Paas

import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Smolok.class)
@Configuration
class EventBusConfigurationTest {

    @Autowired
    EventBus eventBus

    @Bean
    RouteBuilder routeBuilder(Paas paas) {
        paas.reset()
        paas.start()
        def amqpHost = paas.services().find{ it.name == 'eventbus' }.host
        System.setProperty('amqp.host', amqpHost)

        new RouteBuilder() {
            @Override
            void configure() throws Exception {
                from('amqp:echo').log('echo')
            }
        }
    }

    @Test
    void shouldReceiveResponse() {
        def response = eventBus.fromBus('echo', 'bar', String.class)

        assertThat(response).isEqualTo('bar')
    }


}
