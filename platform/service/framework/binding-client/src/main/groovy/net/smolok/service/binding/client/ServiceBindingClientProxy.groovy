package net.smolok.service.binding.client

import smolok.eventbus.client.EventBus
import smolok.eventbus.client.Header

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

import static org.slf4j.LoggerFactory.getLogger

class ServiceBindingClientProxy implements InvocationHandler {

    private static final LOG = getLogger(ServiceBindingClientProxy)

    private final EventBus eventBus

    private final String serviceName

    // Constructors

    ServiceBindingClientProxy(EventBus eventBus, String serviceName) {
        this.eventBus = eventBus
        this.serviceName = serviceName
    }

    // Proxy operations

    @Override
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LOG.debug('About to invoke method {} with arguments: {}', method, args.toList())

        def skippingResult = skipInvocationIfNeeded(method)
        if(skippingResult.present) {
            return skippingResult.get()
        }

        Object body = null
        Header[] headers = []
        if(args != null) {
            body = args.last()
            def remindedArguments = args.toList()
            remindedArguments.remove(args.length - 1)
            headers = Header.arguments(remindedArguments)
        }

        if(method.returnType == Void.TYPE) {
            eventBus.toBusAndWait("${serviceName}.${method.name}", body, headers)
            null
        } else {
            eventBus.fromBus("${serviceName}.${method.name}", body, method.returnType, headers)
        }
    }

    // Helpers

    private Optional<Object> skipInvocationIfNeeded(Method method) {
        if(method.name == 'equals' && method.declaringClass == Object.class) {
            return Optional.of(false)
        }
        Optional.empty()
    }

}
