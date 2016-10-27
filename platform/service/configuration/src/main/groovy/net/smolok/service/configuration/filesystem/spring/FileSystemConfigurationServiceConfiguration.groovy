package net.smolok.service.configuration.filesystem.spring

import net.smolok.service.configuration.api.ConfigurationService
import net.smolok.service.configuration.filesystem.FileSystemConfigurationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import net.smolok.service.binding.ServiceBinding
import net.smolok.service.binding.ServiceEventProcessor

@Configuration
class FileSystemConfigurationServiceConfiguration {

    @Bean(name = 'configuration')
    ConfigurationService configurationService(@Value('${configuration.file:/var/smolok/configuration.properties}') File propertiesFile) {
        new FileSystemConfigurationService(propertiesFile)
    }

    @Bean
    ServiceBinding configurationServiceBinding(ServiceEventProcessor serviceEventProcessor) {
        new ServiceBinding(serviceEventProcessor, 'configuration')
    }

}
