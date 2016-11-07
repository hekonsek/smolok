package net.smolok.service.binding.client.spring

import net.smolok.service.binding.client.BindingClientFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.eventbus.client.EventBus

@Configuration
class BindingClientFactoryConfiguration {

    @Bean
    BindingClientFactory bindingClientFactory(EventBus eventBus) {
        new BindingClientFactory(eventBus)
    }

}
