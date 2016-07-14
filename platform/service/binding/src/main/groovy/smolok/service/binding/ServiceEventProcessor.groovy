package smolok.service.binding

class ServiceEventProcessor {

    private final AuthenticationProvider authenticationProvider

    private final OperationBindingFactory operationBindingFactory

    ServiceEventProcessor(AuthenticationProvider authenticationProvider, OperationBindingFactory operationBindingFactory) {
        this.authenticationProvider = authenticationProvider
        this.operationBindingFactory = operationBindingFactory
    }

    OperationBinding process(ServiceEvent event) {
        def credentials = authenticationProvider.authenticate(event)
        operationBindingFactory.operationBinding(credentials, event.channel(), event.body(), event.headers())
    }

}
