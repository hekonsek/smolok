package smolok.service.binding.camel.spring

import org.apache.camel.CamelContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.service.binding.OperationBindingFactory
import smolok.service.binding.camel.CamelOperationBindingFactory

@Configuration
class CamelServiceBindingConfiguration {

    @Bean
    OperationBindingFactory operationBindingFactory(CamelContext camelContext) {
        new CamelOperationBindingFactory(camelContext.registry)
    }

}
