package net.smolok.service.binding.client

import smolok.eventbus.client.EventBus
import smolok.eventbus.client.Header

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class BindingClientFactory {

    private final EventBus eventBus

    BindingClientFactory(EventBus eventBus) {
        this.eventBus = eventBus
    }

    def <T> T build(Class<T> serviceInterface, String serviceName) {
        Proxy.newProxyInstance(getClass().getClassLoader(), [serviceInterface] as Class<?>[], new ServiceBindingClientProxy(eventBus, serviceName))
    }

}