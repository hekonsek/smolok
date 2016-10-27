package net.smolok.service.device.kapua.spring

import net.smolok.service.device.kapua.HashCodeTenantMapper
import net.smolok.service.device.kapua.KapuaDeviceService
import net.smolok.service.device.kapua.TenantMapper
import org.eclipse.kapua.service.device.registry.DeviceRegistryService
import org.eclipse.kapua.service.device.registry.mongodb.spring.MongodbDeviceRegistryServiceConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import net.smolok.service.binding.ServiceBinding
import net.smolok.service.binding.ServiceEventProcessor

@Configuration
@Import(MongodbDeviceRegistryServiceConfiguration.class)
class KapuaDeviceServiceConfiguration {

    @Bean(name = 'device')
    KapuaDeviceService deviceService(DeviceRegistryService deviceRegistryService, TenantMapper tenantMapper) {
        new KapuaDeviceService(deviceRegistryService, tenantMapper)
    }

    @ConditionalOnMissingBean
    @Bean
    TenantMapper tenantMapper() {
        new HashCodeTenantMapper()
    }

    @Bean
    ServiceBinding serviceBinding(ServiceEventProcessor serviceEventProcessor) {
        new ServiceBinding(serviceEventProcessor, 'device')
    }

}
