package smolok.service.binding

interface OperationBindingFactory {

    OperationBinding operationBinding(Credentials credentials, String channel, Object incomingPayload, Map<String, Object> headers)

}