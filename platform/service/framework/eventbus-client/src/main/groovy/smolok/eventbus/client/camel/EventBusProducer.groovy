package smolok.eventbus.client.camel

import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultProducer

import static smolok.eventbus.client.Header.header

class EventBusProducer extends DefaultProducer {

    EventBusProducer(EventBusEndpoint endpoint) {
        super(endpoint)
    }

    @Override
    void process(Exchange exchange) throws Exception {
        def smolokHeaders =  exchange.in.headers.entrySet().findAll{
            it.key.startsWith('SMOLOK_ARG')
        }.collect{
            header(it.key, it.value)
        }
        log.debug('The following Smolok headers found: {}', smolokHeaders)

        def eventBus = endpoint.resolveEventBus()
        exchange.in.body = eventBus.fromBus(endpoint.resolveChannel(), exchange.in.body, Object.class, *smolokHeaders )
    }

    @Override
    EventBusEndpoint getEndpoint() {
        (EventBusEndpoint) super.getEndpoint()
    }

}
