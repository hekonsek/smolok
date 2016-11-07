package net.smolok.service.binding.client

import smolok.eventbus.client.EventBus
import smolok.eventbus.client.Header

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class ServiceBindingClientProxy implements InvocationHandler {

    private final EventBus eventBus

    private final String serviceName

    ServiceBindingClientProxy(EventBus eventBus, String serviceName) {
        this.eventBus = eventBus
        this.serviceName = serviceName
    }

    @Override
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.name == 'equals' && method.declaringClass == Object.class) {
            return false
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

}
