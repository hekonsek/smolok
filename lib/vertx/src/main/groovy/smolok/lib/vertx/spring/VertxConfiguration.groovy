package smolok.lib.vertx.spring

import io.vertx.core.Vertx
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.vertx.AmqpProbe

@Configuration
class VertxConfiguration {

    @Bean
    Vertx vertx() {
        Vertx.vertx()
    }

    @Bean
    AmqpProbe amqpProbe(Vertx vertx) {
        new AmqpProbe(vertx)
    }

}
