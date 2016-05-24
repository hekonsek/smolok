package smolok.eventbus.client

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory

import static smolok.eventbus.client.Header.arguments

class EventBus {

    private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);

    // Collaborators

    private final ProducerTemplate producerTemplate;

    // Constructors

    EventBus(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate
    }

    // Connector channels API

    void toBus(String channel, Object payload, Header... headers) {
        producerTemplate.sendBodyAndHeaders("amqp:${channel}", payload, arguments(headers))
    }

    void toBus(String channel, Header... headers) {
        toBus(channel, null, headers)
    }

    void toBusAndWait(String channel, Object payload, Header... headers) {
        Map<String, Object> collectedHeaders = new HashMap<>();
        for(Header header : headers) {
            collectedHeaders.put(header.key(), header.value());
        }
        producerTemplate.requestBodyAndHeaders("amqp:" + channel, payload, collectedHeaders)
    }

    void toBusAndWait(String channel) {
        toBusAndWait(channel, null)
    }

    def <T> T fromBus(String channel, Object payload, Class<T> responseType, Header... headers) {
        producerTemplate.requestBodyAndHeaders("amqp:" + channel, payload, arguments(headers), responseType)
    }

    def <T> T fromBus(String channel, Class<T> responseType, Header... headers) {
        fromBus(channel, null, responseType, headers)
    }

    def <T> T pollChannel(String channel, Class<T> responseType) {
        producerTemplate.getCamelContext().createConsumerTemplate().receiveBody("amqp:" + channel, responseType)
    }

}