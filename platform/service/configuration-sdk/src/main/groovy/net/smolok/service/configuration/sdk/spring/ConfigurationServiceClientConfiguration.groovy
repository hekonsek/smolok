package net.smolok.service.configuration.sdk.spring

import net.smolok.service.binding.client.ServiceBindingClientFactory
import net.smolok.service.configuration.api.ConfigurationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ConfigurationServiceClientConfiguration {

    @Bean
    ConfigurationService configurationServiceClient(ServiceBindingClientFactory serviceBindingClientFactory) {
        serviceBindingClientFactory.build(ConfigurationService, 'configuration')
    }

}
