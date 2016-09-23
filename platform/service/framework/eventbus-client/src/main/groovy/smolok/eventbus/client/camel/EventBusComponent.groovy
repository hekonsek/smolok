package smolok.eventbus.client.camel

import org.apache.camel.Endpoint
import org.apache.camel.impl.DefaultComponent

class EventBusComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String s, String remaining, Map<String, Object> map) throws Exception {
        new EventBusEndpoint(remaining, this)
    }

}
