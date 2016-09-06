package net.smolok.service.device.kapua.spring

import com.mongodb.MongoClient
import net.smolok.service.device.kapua.KapuaDeviceService
import org.eclipse.kapua.service.device.registry.DeviceRegistryService
import org.eclipse.kapua.service.device.registry.mongodb.MongoDbDeviceRegistryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.service.binding.ServiceBinding
import smolok.service.binding.ServiceEventProcessor

@Configuration
class KapuaDeviceServiceConfiguration {

    @Bean(name = 'device')
    KapuaDeviceService deviceService(DeviceRegistryService deviceRegistryService) {
        new KapuaDeviceService(deviceRegistryService)
    }

    @Bean
    DeviceRegistryService deviceRegistryService(MongoClient mongo,
                                                @Value('${device.database:smolok}') String db,
                                                @Value('${device.collection:devices}') String collection) {
        new MongoDbDeviceRegistryService(mongo, db, collection)
    }

    @Bean
    ServiceBinding serviceBinding(ServiceEventProcessor serviceEventProcessor) {
        new ServiceBinding(serviceEventProcessor, 'device')
    }

}
