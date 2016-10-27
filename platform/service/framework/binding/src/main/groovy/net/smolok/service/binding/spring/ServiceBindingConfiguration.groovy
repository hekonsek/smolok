package net.smolok.service.binding.spring

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import net.smolok.service.binding.security.AuthenticationProvider
import net.smolok.service.binding.security.MockAutenticationProvider
import net.smolok.service.binding.OperationBindingFactory
import net.smolok.service.binding.ServiceEventProcessor

@Configuration
class ServiceBindingConfiguration {

    @Bean
    ServiceEventProcessor serviceEventProcessor(AuthenticationProvider authenticationProvider, OperationBindingFactory operationBindingFactory) {
        new ServiceEventProcessor(authenticationProvider, operationBindingFactory)
    }

    @Bean
    @ConditionalOnMissingBean
    AuthenticationProvider authenticationProvider() {
        new MockAutenticationProvider('user', 'tenant')
    }

}
