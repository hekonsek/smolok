package smolok.eventbus.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.camel.ConsumerTemplate
import org.apache.camel.ProducerTemplate;

import static org.slf4j.LoggerFactory.getLogger
import static smolok.eventbus.client.Header.arguments
import static smolok.lib.common.Reflections.isPojo

class EventBus {

    // Logger

    private static final LOG = getLogger(EventBus.class)

    // Collaborators

    private final ProducerTemplate producerTemplate

    private final ConsumerTemplate consumerTemplate

    private final mapper = new ObjectMapper()

    // Constructors
    EventBus(ProducerTemplate producerTemplate, ConsumerTemplate consumerTemplate) {
        this.producerTemplate = producerTemplate
        this.consumerTemplate = consumerTemplate
    }

    // Connector channels API

    void toBus(String channel, Object payload, Header... headers) {
        LOG.debug('Sending payload {} to channel {} with headers {}', payload, channel, headers.toList())
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
        convertResponse(producerTemplate.requestBodyAndHeaders("amqp:" + channel, payload, arguments(headers)), responseType)
    }

    def <T> T fromBus(String channel, Class<T> responseType, Header... headers) {
        convertResponse(fromBus(channel, null, headers), responseType)
    }

    def <T> T pollChannel(String channel, Class<T> responseType) {
        LOG.debug('Polling channel {}. Expecting type {}.', channel, responseType)
        convertResponse(consumerTemplate.receiveBody("amqp:${channel}"), responseType)
    }

    // Helpers

    /**
     * This is workaround needed due to the fact that Qpid JMS client doesn't support nested maps.
     */
    private <T> T convertResponse(Object body, Class<T> responseType) {
        if(body.class == byte[].class && (Map.class.isAssignableFrom(responseType) || isPojo(responseType))) {
            mapper.readValue((byte[])body, responseType)
        } else {
            mapper.convertValue(body, responseType)
        }
    }

}