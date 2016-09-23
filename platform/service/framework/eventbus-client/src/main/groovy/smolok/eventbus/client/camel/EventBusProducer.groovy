package smolok.eventbus.client.camel

import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultProducer

class EventBusProducer extends DefaultProducer {

    EventBusProducer(EventBusEndpoint endpoint) {
        super(endpoint)
    }

    @Override
    void process(Exchange exchange) throws Exception {
        exchange.in.body = endpoint.resolveEventBus().fromBus(endpoint.resolveChannel(), exchange.in.body, Object.class)
    }

    @Override
    EventBusEndpoint getEndpoint() {
        (EventBusEndpoint) super.getEndpoint()
    }

}
