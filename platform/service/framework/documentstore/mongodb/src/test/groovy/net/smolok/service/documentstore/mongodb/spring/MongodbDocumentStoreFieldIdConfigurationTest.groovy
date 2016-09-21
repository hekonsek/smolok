/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.smolok.service.documentstore.mongodb.spring

import com.fasterxml.jackson.databind.ObjectMapper
import net.smolok.service.documentstore.api.DocumentStore
import net.smolok.service.documentstore.api.QueryBuilder
import org.bson.types.ObjectId
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static org.assertj.core.api.Assertions.assertThat
import static org.joda.time.DateTime.now
import static smolok.lib.common.Networks.findAvailableTcpPort
import static smolok.lib.common.Properties.setIntProperty
import static smolok.lib.common.Properties.setSystemStringProperty
import static smolok.lib.common.Uuids.uuid

@RunWith(SpringRunner.class)
@SpringBootTest(classes = [MongoAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class,
        MongodbDocumentStoreConfiguration.class, MongodbDocumentStoreFieldIdConfigurationTest.class])
class MongodbDocumentStoreFieldIdConfigurationTest {

    def collection = uuid()

    def invoice = new Invoice()

    def mapper = new ObjectMapper()

    @Autowired
    DocumentStore documentStore

    @BeforeClass
    static void beforeClass() {
        setSystemStringProperty('documentStore.mongodb.idField', 'myid')
        setIntProperty("spring.data.mongodb.port", findAvailableTcpPort())
    }

    // Tests

    @Test
    void shouldFindOneCustomId() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedInvoice = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedInvoice.myid).isNotNull()
    }

    @Test
    void shouldFindOneByQuery() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def invoices = documentStore.find(collection, new QueryBuilder([myid: id]))

        // Then
        assertThat(invoices).isNotEmpty()
    }

    // Helpers

    private Map<String, Object> serialize(Invoice invoice) {
        mapper.convertValue(invoice, Map.class)
    }


    // Class fixtures

    static class Invoice {

        String myid

    }

}