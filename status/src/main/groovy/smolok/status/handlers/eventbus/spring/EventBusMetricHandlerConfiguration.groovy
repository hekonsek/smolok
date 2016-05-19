package smolok.status.handlers.eventbus.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.vertx.AmqpProbe
import smolok.status.handlers.eventbus.EventBusMetricHandler

@Configuration
class EventBusMetricHandlerConfiguration {

    @Bean
    EventBusMetricHandler eventBusMetricHandler(AmqpProbe amqpProbe) {
        new EventBusMetricHandler(amqpProbe)
    }

}
