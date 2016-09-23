package org.eclipse.kapua.service.device.registry.mongodb.spring

import com.mongodb.Mongo
import net.smolok.service.documentstore.api.DocumentStore
import org.eclipse.kapua.service.device.registry.DeviceRegistryService
import org.eclipse.kapua.service.device.registry.mongodb.DocumentStoreDeviceRegistryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongodbDeviceRegistryServiceConfiguration {

    @Bean
    DeviceRegistryService deviceRegistryService(DocumentStore documentStore, Mongo mongo,
                                                @Value('${device.database:documents}') String db,
                                                @Value('${device.collection:devices}') String collection) {
        new DocumentStoreDeviceRegistryService(documentStore, mongo, db, collection)
    }

}