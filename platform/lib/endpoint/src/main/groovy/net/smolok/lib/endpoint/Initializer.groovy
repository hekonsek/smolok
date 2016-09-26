package net.smolok.lib.endpoint

import org.apache.camel.CamelContext

interface Initializer {

    def initialize(CamelContext camelContext)

}