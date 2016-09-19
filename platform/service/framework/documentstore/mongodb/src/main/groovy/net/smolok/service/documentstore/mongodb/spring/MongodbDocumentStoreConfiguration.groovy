package net.smolok.service.documentstore.mongodb.spring

import com.mongodb.Mongo
import net.smolok.service.documentstore.api.DocumentStore
import net.smolok.service.documentstore.mongodb.MongodbDocumentStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongodbDocumentStoreConfiguration {

    @Bean
    DocumentStore documentStore(Mongo mongo) {
        new MongodbDocumentStore(mongo, 'docs')
    }

}
