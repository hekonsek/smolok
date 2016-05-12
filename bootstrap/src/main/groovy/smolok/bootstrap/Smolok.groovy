package smolok.bootstrap

import org.apache.camel.component.amqp.AMQPComponent
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean

import static org.apache.camel.component.amqp.AMQPComponent.amqpComponent

@SpringBootApplication(scanBasePackages = 'smolok')
class Smolok {

    @Bean
    AMQPComponent amqp(@Value('${amqp.port:5672}') int port) {
        amqpComponent("failover:(amqp://localhost:${port})")
    }

    // Main execution point

    public static void main(String[] args) {
        new SpringApplicationBuilder(Smolok.class).run(args)
    }

}