package smolok.lib.vertx

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.proton.ProtonConnection
import io.vertx.proton.ProtonDelivery
import io.vertx.proton.ProtonMessageHandler
import io.vertx.proton.ProtonReceiver
import io.vertx.proton.ProtonServer
import io.vertx.proton.ProtonSession
import org.apache.qpid.proton.message.Message

class AmqpServer {

    private final Vertx vertx

    private final int port

    private final Map<String, List<Object>> messages = [:]

    AmqpServer(Vertx vertx, int port) {
        this.vertx = vertx
        this.port = port
    }

    void start() {
        ProtonServer.create(vertx).connectHandler(new Handler<ProtonConnection>() {
            @Override
            void handle(ProtonConnection connection) {
                connection.open().sessionOpenHandler(new Handler<ProtonSession>() {
                    @Override
                    void handle(ProtonSession protonSession) {
                        protonSession.open();
                    }
                }).receiverOpenHandler { ProtonReceiver receiver ->
                    receiver.handler { ProtonDelivery delivery, Message msg ->
                        messages.get(msg.address, []).add(msg)
                    }.open()
                }
            }
        }).listen(port)
    }

    Map<String, List<Object>> getMessages() {
        return messages
    }

}
