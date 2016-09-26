package net.smolok.lib.endpoint.converters

import net.smolok.lib.endpoint.RequestConverter

class GroovyRequestConverter implements RequestConverter {

    @Override
    boolean supports(String payload) {
        payload.startsWith('groovy:')
    }

    @Override
    Object convert(String payload) {
        if(payload.startsWith('groovy:')) {
            payload = payload.substring(7)
        }
        new GroovyShell().evaluate(payload)
    }

}
