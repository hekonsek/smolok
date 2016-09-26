package net.smolok.lib.endpoint.spring

import net.smolok.lib.endpoint.Endpoint
import net.smolok.lib.endpoint.Initializer
import net.smolok.lib.endpoint.RequestConverter
import net.smolok.lib.endpoint.converters.GroovyRequestConverter
import net.smolok.lib.endpoint.initializers.AmqpInitializer
import org.apache.camel.ProducerTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EndpointConfiguration {

    @Bean(initMethod = 'start')
    Endpoint endpoint(ProducerTemplate producerTemplate, List<RequestConverter> requestConverters, GroovyRequestConverter groovyRequestConverter, List<Initializer> initializers) {
        new Endpoint(producerTemplate, requestConverters, groovyRequestConverter, initializers)
    }

    @Bean
    GroovyRequestConverter groovyRequestConverter() {
        new GroovyRequestConverter()
    }

    @Bean
    AmqpInitializer amqpInitializer() {
        new AmqpInitializer()
    }

}
