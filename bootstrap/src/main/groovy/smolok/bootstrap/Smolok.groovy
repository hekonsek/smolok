package smolok.bootstrap

import org.apache.camel.component.amqp.AMQPComponent
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean

import static org.apache.camel.component.amqp.AMQPComponent.amqpComponent

/**
 * Boostraps Spring Boot application capable of connecting to the Smolok event bus. The application loads all Smolok
 * modules available in a classpath.
 */
@SpringBootApplication(scanBasePackages = 'smolok')
class Smolok {

    // Event bus connectivity

    @Bean
    AMQPComponent amqp(
            @Value('${amqp.host:localhost}') String host,
            @Value('${amqp.port:5672}') int port) {
        amqpComponent("failover:(amqp://${host}:${port})")
    }

    // Main execution point

    public static void main(String[] args) {
        new SpringApplicationBuilder(Smolok.class).run(args)
    }

}