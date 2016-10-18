package smolok.lib.vertx.spring

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.proton.ProtonConnection
import io.vertx.proton.ProtonDelivery
import io.vertx.proton.ProtonMessageHandler
import io.vertx.proton.ProtonReceiver
import io.vertx.proton.ProtonServer
import io.vertx.proton.ProtonSession
import org.apache.qpid.proton.message.Message
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.lib.vertx.AmqpProbe
import smolok.lib.vertx.AmqpServer

import java.util.concurrent.Callable

import static com.jayway.awaitility.Awaitility.await
import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = [VertxConfigurationTest, VertxConfiguration])
class VertxConfigurationTest {

    // Tests subject

    @Autowired
    AmqpProbe amqpProbe

    // Collaborators

    @Autowired
    Vertx vertx

    // Configuration fixtures

    static def port = findAvailableTcpPort()

    @Bean(initMethod = 'start')
    AmqpServer amqpServer(Vertx vertx) {
        new AmqpServer(vertx, port)
    }

    @Autowired
    AmqpServer amqpServer

    // Tests

    @Test
    void shouldNotConnectToAmqp() {
        // When
        def canSend = amqpProbe.canSendMessageTo('localhost', 6666)

        // Then
        assertThat(canSend).isFalse()
    }

    @Test
    void shouldConnectToAmqp() {
        // Then
        await().until({ amqpProbe.canSendMessageTo('localhost', port) } as Callable<Boolean>)
    }

    @Test
    void shouldSendMessageToAmqpServer() {
        // Then
        amqpProbe.send('localhost', port, 'xxx', 'yyy')
        await().until({ amqpServer.messages.get('xxx') != null  } as Callable<Boolean>)
    }

}
