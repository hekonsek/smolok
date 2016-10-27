package net.smolok.service.binding

class ServiceBindingFactory {

    private final ServiceEventProcessor serviceEventProcessor

    ServiceBindingFactory(ServiceEventProcessor serviceEventProcessor) {
        this.serviceEventProcessor = serviceEventProcessor
    }

    ServiceBinding serviceBinding(String channel) {
        new ServiceBinding(serviceEventProcessor, channel)
    }

}