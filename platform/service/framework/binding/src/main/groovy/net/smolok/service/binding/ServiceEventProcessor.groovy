package net.smolok.service.binding

import net.smolok.service.binding.security.AuthenticationProvider

import static net.smolok.service.binding.security.CredentialsHolder.bindCredentials

class ServiceEventProcessor {

    private final AuthenticationProvider authenticationProvider

    private final OperationBindingFactory operationBindingFactory

    ServiceEventProcessor(AuthenticationProvider authenticationProvider, OperationBindingFactory operationBindingFactory) {
        this.authenticationProvider = authenticationProvider
        this.operationBindingFactory = operationBindingFactory
    }

    OperationBinding onEvent(ServiceEvent event) {
        def credentials = authenticationProvider.authenticate(event)
        bindCredentials(credentials)
        operationBindingFactory.operationBinding(credentials, event)
    }

}
