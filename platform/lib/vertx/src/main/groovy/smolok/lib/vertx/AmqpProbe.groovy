package smolok.lib.vertx

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.proton.*
import org.apache.qpid.proton.amqp.messaging.AmqpValue
import org.apache.qpid.proton.message.Message
import smolok.lib.common.Uuids

import java.util.concurrent.CountDownLatch

import static java.util.concurrent.TimeUnit.SECONDS
import static org.apache.commons.lang3.Validate.isTrue

class AmqpProbe {

    // Constants

    private final static STATUS_CHANNEL = 'status'

    // Collaborators

    private final Vertx vertx

    // Constructors

    AmqpProbe(Vertx vertx) {
        this.vertx = vertx
    }

    boolean canSendMessageTo(String host, int port) {
        try {
            amqpExchange(host, port, STATUS_CHANNEL, 'ping', null)
            true
        } catch (IllegalStateException e) {
            false
        }
    }

    void send(String host, int port, String channel, Object body) {
        amqpExchange(host, port, channel, body, null)
    }

    Object request(String host, int port, String channel, Object body) {
        amqpExchange(host, port, channel, body, Object.class)
    }

    // Helpers

    private <T> T amqpExchange(String host, int port, String channel, Object body, Class<T> responseType) {
        def responseAvailable = new CountDownLatch(responseType == null ? 1 : 2)
        def delivered = false
        T response = null

        ProtonClient.create(vertx).connect(host, port, new Handler<AsyncResult<ProtonConnection>>() {
            @Override
            void handle(AsyncResult<ProtonConnection> connectionResponse) {
                if(connectionResponse.succeeded()) {
                    def replyTo = Uuids.uuid()
                    def message = Message.Factory.create()
                    if(responseType != null) {
                        message.setReplyTo(replyTo)
                    }
                    message.setAddress(channel)
                    message.body = new AmqpValue(body)
                    connectionResponse.result().open().createSender(channel).open().send(message) { ProtonDelivery protonDelivery ->
                        delivered = true
                        responseAvailable.countDown()
                    }

                    if(responseType != null) {
                        connectionResponse.result().open().sessionOpenHandler(new Handler<ProtonSession>() {
                            @Override
                            void handle(ProtonSession protonSession) {
                                protonSession.open();
                            }
                        }).receiverOpenHandler { ProtonReceiver receiver ->
                            if(receiver.remoteTarget.address == replyTo) {
                                receiver.handler { ProtonDelivery delivery, Message msg ->
                                    response = ((AmqpValue) msg.body).value
                                    responseAvailable.countDown()

                                }.open()
                            }
                        }
                    }
                } else {
                    responseAvailable.countDown()
                }
            }
        })

        try {
            responseAvailable.await(15, SECONDS)
            if(!(delivered && responseAvailable.count == 0)) {
                throw new IllegalStateException()
            }
            responseType == null ? null : response
        } catch (InterruptedException e) {
            throw new IllegalStateException()
        }
    }

}
