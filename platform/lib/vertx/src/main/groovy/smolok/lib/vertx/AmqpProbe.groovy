package smolok.lib.vertx

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.proton.ProtonClient
import io.vertx.proton.ProtonConnection
import io.vertx.proton.ProtonDelivery
import io.vertx.proton.ProtonReceiver
import io.vertx.proton.ProtonSession
import org.apache.commons.lang3.Validate
import org.apache.qpid.proton.amqp.messaging.AmqpValue
import org.apache.qpid.proton.amqp.messaging.Header
import org.apache.qpid.proton.message.Message
import smolok.lib.common.Uuids

import java.util.concurrent.CountDownLatch

import static java.util.concurrent.TimeUnit.SECONDS

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
        def responseAvailable = new CountDownLatch(1)
        def delivered = false

        connect(host, port, new Handler<AsyncResult<ProtonConnection>>() {
            @Override
            void handle(AsyncResult<ProtonConnection> connectionResponse) {
                if(connectionResponse.succeeded()) {
                    def message = Message.Factory.create()
                    message.body = new AmqpValue('ping')
                    connectionResponse.result().open().
                            createSender(STATUS_CHANNEL).open().send(message, new Handler<ProtonDelivery>() {
                        @Override
                        void handle(ProtonDelivery protonDelivery) {
                            delivered = true
                            responseAvailable.countDown()
                        }
                    })
                } else {
                    responseAvailable.countDown()
                }
            }
        })

        try {
            responseAvailable.await(15, SECONDS)
            delivered && responseAvailable.count == 0
        } catch (InterruptedException e) {
            false
        }
    }

    void send(String host, int port, String channel, Object body) {
        def responseAvailable = new CountDownLatch(1)
        def delivered = false

        connect(host, port, new Handler<AsyncResult<ProtonConnection>>() {
            @Override
            void handle(AsyncResult<ProtonConnection> connectionResponse) {
                if(connectionResponse.succeeded()) {
                    def message = Message.Factory.create()
                    message.setAddress(channel)
                    message.body = new AmqpValue(body)
                    connectionResponse.result().open().
                            createSender(channel).open().send(message, new Handler<ProtonDelivery>() {
                        @Override
                        void handle(ProtonDelivery protonDelivery) {
                            delivered = true
                            responseAvailable.countDown()
                        }
                    })
                } else {
                    responseAvailable.countDown()
                }
            }
        })

        try {
            responseAvailable.await(15, SECONDS)
            Validate.isTrue(delivered)
        } catch (InterruptedException e) {
            Validate.isTrue(delivered)
        }
    }

    Object request(String host, int port, String channel, Object body) {
        def responseAvailable = new CountDownLatch(1)
        Object response = null

        connect(host, port, new Handler<AsyncResult<ProtonConnection>>() {
            @Override
            void handle(AsyncResult<ProtonConnection> connectionResponse) {
                if(connectionResponse.succeeded()) {
                    def message = Message.Factory.create()
                    def replyTo = Uuids.uuid()
                    message.setReplyTo(replyTo)
                    message.setAddress(channel)
                    message.body = new AmqpValue(body)

                    connectionResponse.result().open().
                            createSender(channel).open().send(message, new Handler<ProtonDelivery>() {
                        @Override
                        void handle(ProtonDelivery protonDelivery) {
//                            delivered = true
//                            responseAvailable.countDown()
                        }
                    })

                    connectionResponse.result().open().sessionOpenHandler(new Handler<ProtonSession>() {
                        @Override
                        void handle(ProtonSession protonSession) {
                            protonSession.open();
                        }
                    }).receiverOpenHandler { ProtonReceiver receiver ->
                        receiver.handler { ProtonDelivery delivery, Message msg ->
                            response = ((AmqpValue) msg.body).value
                            responseAvailable.countDown()

                        }.open()
                    }
                } else {
                    responseAvailable.countDown()
                }
            }
        })

        try {
            responseAvailable.await(15, SECONDS)
            response
        } catch (InterruptedException e) {
            response
        }
    }

    // Helpers

    private void connect(String host, int port, Handler<AsyncResult<ProtonConnection>> connectionHandler) {
        ProtonClient.create(vertx).connect(host, port, connectionHandler)
    }

}
