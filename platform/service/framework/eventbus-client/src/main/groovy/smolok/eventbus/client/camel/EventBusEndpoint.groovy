package smolok.eventbus.client.camel

import org.apache.camel.Component
import org.apache.camel.Consumer
import org.apache.camel.Processor
import org.apache.camel.Producer
import org.apache.camel.impl.DefaultEndpoint
import smolok.eventbus.client.EventBus

class EventBusEndpoint extends DefaultEndpoint {

    private EventBus eventBus

    EventBusEndpoint(String endpointUri, Component component) {
        super(endpointUri, component)
    }

    @Override
    Producer createProducer() throws Exception {
        new EventBusProducer(this)
    }

    @Override
    Consumer createConsumer(Processor processor) throws Exception {
        return null
    }

    @Override
    boolean isSingleton() {
        false
    }

    EventBus getEventBus() {
        return eventBus
    }

    void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus
    }

    EventBus resolveEventBus() {
        if(eventBus != null) {
            return eventBus
        }

        def eventBuses = component.camelContext.registry.findByType(EventBus.class)
        if(!eventBuses.isEmpty()) {
            return eventBuses.first()
        }

        null
    }

    String resolveChannel() {
        return endpointUri
    }

}