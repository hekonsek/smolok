package smolok.service.binding

import smolok.service.binding.security.Credentials

interface OperationBindingFactory {

    OperationBinding operationBinding(Credentials credentials, String channel, Object incomingPayload, Map<String, Object> headers)

}