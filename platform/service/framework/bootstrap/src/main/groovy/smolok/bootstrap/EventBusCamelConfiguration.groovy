package smolok.bootstrap

import org.apache.camel.component.amqp.AMQPComponent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static org.apache.camel.component.amqp.AMQPComponent.amqpComponent

@Configuration
class EventBusCamelConfiguration {

    /**
     * Camel AMQP component pre-configured to connect to the Smolok event bus.
     */
    @Bean
    AMQPComponent amqp(
            @Value('${EVENTBUS_SERVICE_HOST:localhost}') String host,
            @Value('${EVENTBUS_SERVICE_PORT:5672}') int port) {
        amqpComponent("failover:(amqp://${host}:${port})")
    }

}
