package smolok.encoding.json.spring

import smolok.encoding.json.JsonPayloadEncoding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration
import smolok.encoding.spi.PayloadEncoding;

/**
 * Spring configuration loading and configuring JSON payload encoding.
 */
@Configuration
class JsonPayloadEncodingConfiguration {

    @Bean
    PayloadEncoding payloadEncoding() {
        new JsonPayloadEncoding()
    }

}