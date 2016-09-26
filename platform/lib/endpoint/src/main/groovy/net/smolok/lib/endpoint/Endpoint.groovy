package net.smolok.lib.endpoint

import org.apache.camel.ProducerTemplate

import static com.google.common.base.MoreObjects.firstNonNull

class Endpoint {

    private final ProducerTemplate producerTemplate

    List<RequestConverter> requestConverters

    RequestConverter defaultRequestConverter

    Endpoint(ProducerTemplate producerTemplate, List<RequestConverter> requestConverters, RequestConverter defaultRequestConverter) {
        this.producerTemplate = producerTemplate
        this.requestConverters = requestConverters
        this.defaultRequestConverter = defaultRequestConverter
    }

    // Endpoint operations

    def String request(String endpoint, String payload, String responseType) {
        def requestConverter = firstNonNull(requestConverters.find { it.supports(payload) }, defaultRequestConverter)
        def convertedPayload = requestConverter.convert(payload)

        Class responseClass
        Closure converter = null
        if (responseType == null) {
            responseClass = String.class
        } else if (responseType == 'groovy') {
            responseClass = Object.class
            converter = { new GroovyShell().evaluate(it.toString()) }
        }

        def response = producerTemplate.requestBody(endpoint, convertedPayload, responseClass)

        if (converter != null) {
            response = converter(response)
        }
        response.toString()
    }

    def String request(String endpoint, String payload) {
        request(endpoint, payload, null)
    }

}