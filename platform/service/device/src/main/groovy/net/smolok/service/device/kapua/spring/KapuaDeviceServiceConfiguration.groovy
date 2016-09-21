package net.smolok.service.device.kapua.spring

import net.smolok.service.device.kapua.KapuaDeviceService
import org.eclipse.kapua.service.device.registry.DeviceRegistryService
import org.eclipse.kapua.service.device.registry.mongodb.spring.MongodbDeviceRegistryServiceConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import smolok.service.binding.ServiceBinding
import smolok.service.binding.ServiceEventProcessor

@Configuration
@Import(MongodbDeviceRegistryServiceConfiguration.class)
class KapuaDeviceServiceConfiguration {

    @Bean(name = 'device')
    KapuaDeviceService deviceService(DeviceRegistryService deviceRegistryService) {
        new KapuaDeviceService(deviceRegistryService)
    }

    @Bean
    ServiceBinding serviceBinding(ServiceEventProcessor serviceEventProcessor) {
        new ServiceBinding(serviceEventProcessor, 'device')
    }

}
