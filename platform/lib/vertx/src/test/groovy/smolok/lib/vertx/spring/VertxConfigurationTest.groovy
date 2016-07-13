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
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.lib.vertx.AmqpProbe

import java.util.concurrent.Callable

import static com.jayway.awaitility.Awaitility.await
import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VertxConfiguration.class)
class VertxConfigurationTest {

    // Tests subject

    @Autowired
    AmqpProbe amqpProbe

    // Collaborators

    @Autowired
    Vertx vertx

    // Configuration fixtures

    def port = findAvailableTcpPort()

    // Tests

    @Test
    void shouldNotConnectToAmqp() {
        // When
        def canSend = amqpProbe.canSendMessageTo('localhost', port)

        // Then
        assertThat(canSend).isFalse()
    }

    @Test
    void shouldConnectToAmqp() {
        // Given
        startProtonServer()

        // Then
        await().until({ amqpProbe.canSendMessageTo('localhost', port) } as Callable<Boolean>)
    }

    // Helpers

    void startProtonServer() {
        ProtonServer.create(vertx).connectHandler(new Handler<ProtonConnection>() {
            @Override
            void handle(ProtonConnection connection) {
                connection.open().sessionOpenHandler(new Handler<ProtonSession>() {
                    @Override
                    void handle(ProtonSession protonSession) {
                        protonSession.open();
                    }
                }).receiverOpenHandler(new Handler<ProtonReceiver>() {
                    @Override
                    void handle(ProtonReceiver receiver) {
                        receiver.handler(new ProtonMessageHandler() {
                            @Override
                            void handle(ProtonDelivery delivery, Message msg) {}
                        }).open()
                    }
                })
            }
        }).listen(port)
    }

}
