package net.smolok.lib.endpoint.initializers

import net.smolok.lib.endpoint.Initializer
import org.apache.camel.CamelContext

import static org.apache.camel.component.amqp.AMQPComponent.amqpComponent
import static smolok.lib.common.Properties.intProperty
import static smolok.lib.common.Properties.stringProperty

class AmqpInitializer implements Initializer {

    def initialize(CamelContext camelContext) {
        def host = stringProperty('AMQP_HOST', 'localhost')
        def port = intProperty('AMQP_PORT', 5672)
        def amqp = amqpComponent("failover:(amqp://${host}:${port})")
        camelContext.addComponent('amqp', amqp)
    }

}
