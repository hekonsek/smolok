package net.smolok.service.binding

import net.smolok.service.binding.security.Credentials

interface OperationBindingFactory {

    OperationBinding operationBinding(Credentials credentials, String channel, Object incomingPayload, Map<String, Object> headers)

}