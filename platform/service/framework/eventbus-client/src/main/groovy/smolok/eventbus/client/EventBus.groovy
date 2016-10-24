package smolok.eventbus.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.camel.ConsumerTemplate
import org.apache.camel.ProducerTemplate

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import static org.slf4j.LoggerFactory.getLogger
import static smolok.eventbus.client.Header.arguments
import static smolok.lib.common.Reflections.isContainer
import static smolok.lib.common.Reflections.isPojo

/**
 * Java client for Smolok event bus. Provides higher-level library on the top of the AMQP communication used by Smolok event
 * bus. It can be used to consume and interact with Smolok Cloud public APIs.
 */
class EventBus {

    // Logger

    private static final LOG = getLogger(EventBus.class)

    // Collaborators

    private final ProducerTemplate producerTemplate

    private final ConsumerTemplate consumerTemplate

    private final mapper = new ObjectMapper().setSerializationInclusion(NON_NULL)

    // Constructors

    EventBus(ProducerTemplate producerTemplate, ConsumerTemplate consumerTemplate) {
        this.producerTemplate = producerTemplate
        this.consumerTemplate = consumerTemplate
    }

    // Connector channels API

    void toBus(String channel, Object payload, Header... headers) {
        LOG.debug('Sending payload {} to channel {} with headers {}', payload, channel, headers.toList())
        producerTemplate.sendBodyAndHeaders("amqp:${channel}", serializePayload(payload), arguments(headers))
    }

    void toBus(String channel, Header... headers) {
        toBus(channel, null, headers)
    }

    void toBusAndWait(String channel, Object payload, Header... headers) {
        LOG.debug('Payload {} has been sent to bus channel {} with wait request.', payload, channel)
        def collectedHeaders = [:]
        for(Header header : headers) {
            collectedHeaders[header.key()] = header.value()
        }
        producerTemplate.requestBodyAndHeaders("amqp:" + channel, serializePayload(payload), collectedHeaders)
    }

    void toBusAndWait(String channel) {
        toBusAndWait(channel, null)
    }

    def <T> T fromBus(String channel, Object payload, Class<T> responseType, Header... headers) {
        convertResponse(producerTemplate.requestBodyAndHeaders("amqp:" + channel, serializePayload(payload), arguments(headers)), responseType)
    }

    def <T> T fromBus(String channel, Class<T> responseType, Header... headers) {
        fromBus(channel, null, responseType, headers)
    }

    def <T> T pollChannel(String channel, Class<T> responseType) {
        LOG.debug('Polling channel {}. Expecting type {}.', channel, responseType)
        convertResponse(consumerTemplate.receiveBody("amqp:${channel}"), responseType)
    }

    // Those are workarounds needed due to the fact that Qpid JMS client doesn't support nested maps and collections

    private Object serializePayload(Object payload) {
        if(payload != null && (isContainer(payload.getClass()) || isPojo(payload.getClass()))) {
            mapper.writeValueAsBytes(payload)
        } else {
            payload
        }
    }

    private <T> T convertResponse(Object body, Class<T> responseType) {
        if(body == null) {
            return null
        }

        if(body.class == byte[].class && (isContainer(responseType) || isPojo(responseType))) {
            mapper.readValue((byte[])body, responseType)
        } else {
            mapper.convertValue(body, responseType)
        }
    }

}