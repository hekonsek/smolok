package smolok.encoding.camel.spring;

import org.assertj.core.api.Assertions
import org.eclipse.kapua.locator.spring.KapuaApplication;
import smolok.encoding.camel.PayloadEncodingDataFormat
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KapuaApplication.class)
@Configuration
class PayloadEncodingDataFormatConfigurationTest {

    @Autowired
    ProducerTemplate producerTemplate

    @Bean
    RoutesBuilder routeBuilder(PayloadEncodingDataFormat dataFormat) {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:test").marshal(dataFormat).unmarshal(dataFormat);
            }
        }
    }

    // Tests

    @Test
    void shouldMarshalAndUnmarshalPayload() {
        def payload = 'payload'
        def result = producerTemplate.requestBody("direct:test", payload, String.class);
        Assertions.assertThat(result).isEqualTo(payload);
    }

}