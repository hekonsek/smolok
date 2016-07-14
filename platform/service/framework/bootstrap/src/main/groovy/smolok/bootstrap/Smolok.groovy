package smolok.bootstrap

import org.apache.camel.component.amqp.AMQPComponent
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean

import static org.apache.camel.component.amqp.AMQPComponent.amqpComponent
import static org.slf4j.LoggerFactory.getLogger

/**
 * Boostraps Spring Boot application capable of connecting to the Smolok event bus. The application loads all Smolok
 * modules available in a classpath.
 */
@SpringBootApplication(scanBasePackages = 'smolok')
class Smolok {

    protected final log = getLogger(getClass())

    // Event bus connectivity

    /**
     * Camel AMQP component pre-configured to connect to the Smolok event bus.
     */
    @Bean
    AMQPComponent amqp(
            @Value('${amqp.host:localhost}') String host,
            @Value('${amqp.port:5672}') int port) {
        amqpComponent("failover:(amqp://${host}:${port})")
    }

    // Execution points

    ConfigurableApplicationContext run(String[] args) {
        new SpringApplicationBuilder(Smolok.class).run(args)
    }

    static void main(String... args) {
        new Smolok().run(args)
    }

}