package smolok.lib.vertx

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.proton.ProtonClient
import io.vertx.proton.ProtonConnection
import io.vertx.proton.ProtonDelivery
import org.apache.commons.lang3.Validate
import org.apache.qpid.proton.amqp.messaging.AmqpValue
import org.apache.qpid.proton.amqp.messaging.Header
import org.apache.qpid.proton.message.Message

import java.util.concurrent.CountDownLatch

import static java.util.concurrent.TimeUnit.SECONDS

class AmqpProbe {

    // Constants

    private final static STATUS_CHANNEL = 'status'

    private final Vertx vertx

    AmqpProbe(Vertx vertx) {
        this.vertx = vertx
    }

    boolean canSendMessageTo(String host, int port) {
        def responseAvailable = new CountDownLatch(1)
        def delivered = false

        ProtonClient.create(vertx).connect(host, port, new Handler<AsyncResult<ProtonConnection>>() {
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

    void send(String host, int port, String channel, String body) {
        def responseAvailable = new CountDownLatch(1)
        def delivered = false

        ProtonClient.create(vertx).connect(host, port, new Handler<AsyncResult<ProtonConnection>>() {
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

}
