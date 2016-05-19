package smolok.status.handlers.eventbus.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.status.handlers.eventbus.EventBusStatusHandler

@Configuration
class EventBusStatusHandlerConfiguration {

    @Bean
    EventBusStatusHandler eventBusTcpEndpointStatusSubjectHandler() {
        new EventBusStatusHandler()
    }

}
