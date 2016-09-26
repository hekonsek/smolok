package net.smolok.lib.endpoint.spring

import net.smolok.lib.endpoint.Endpoint
import net.smolok.lib.endpoint.RequestConverter
import net.smolok.lib.endpoint.converters.GroovyRequestConverter
import org.apache.camel.ProducerTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EndpointConfiguration {

    @Bean
    Endpoint endpoint(ProducerTemplate producerTemplate, List<RequestConverter> requestConverters, GroovyRequestConverter groovyRequestConverter) {
        new Endpoint(producerTemplate, requestConverters, groovyRequestConverter)
    }

    @Bean
    GroovyRequestConverter groovyRequestConverter() {
        new GroovyRequestConverter()
    }

}
