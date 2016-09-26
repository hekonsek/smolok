package net.smolok.lib.endpoint

import org.apache.camel.ProducerTemplate

import static com.google.common.base.MoreObjects.firstNonNull

class Endpoint {

    private final ProducerTemplate producerTemplate

    private final List<RequestConverter> requestConverters

    private final RequestConverter defaultRequestConverter

    private final List<Initializer> initializers

    Endpoint(ProducerTemplate producerTemplate, List<RequestConverter> requestConverters, RequestConverter defaultRequestConverter, List<Initializer> initializers) {
        this.producerTemplate = producerTemplate
        this.requestConverters = requestConverters
        this.defaultRequestConverter = defaultRequestConverter
        this.initializers = initializers
    }

    void start() {
        initializers.each { it.initialize(producerTemplate.camelContext) }
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