package smolok.eventbus.spring

import org.apache.activemq.broker.BrokerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for EventBus service. Current implementation is based on ActiveMQ.
 */
@Configuration
class EventBusConfiguration {

    @Bean(initMethod = 'start', destroyMethod = 'stop')
    BrokerService broker(
            @Value('${amqp.host:0.0.0.0}') String host,
            @Value('${amqp.port:5672}') int port) {
        def broker = new BrokerService()
        broker.setPersistent(false)
        broker.addConnector("amqp://${host}:${port}")
        broker
    }

}