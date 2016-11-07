package net.smolok.service.binding.client.spring

import net.smolok.service.binding.client.ServiceBindingClientFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.eventbus.client.EventBus

@Configuration
class BindingClientFactoryConfiguration {

    @Bean
    ServiceBindingClientFactory bindingClientFactory(EventBus eventBus) {
        new ServiceBindingClientFactory(eventBus)
    }

}
