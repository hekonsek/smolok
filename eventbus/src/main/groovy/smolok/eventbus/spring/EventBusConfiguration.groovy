package smolok.eventbus.spring

import org.apache.activemq.broker.BrokerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventBusConfiguration {

    @Bean(initMethod = 'start', destroyMethod = 'stop')
    BrokerService broker(@Value('${amqp.port:5672}') int port) {
        def broker = new BrokerService()
        broker.setPersistent(false)
        broker.addConnector("amqp://0.0.0.0:${port}")
        broker
    }

}