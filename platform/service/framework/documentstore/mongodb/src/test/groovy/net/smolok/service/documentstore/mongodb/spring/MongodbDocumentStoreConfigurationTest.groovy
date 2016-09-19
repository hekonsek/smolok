/**
 * Licensed to the Rhiot under one or more
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
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Networks.findAvailableTcpPort
import static smolok.lib.common.Properties.setIntProperty
import static smolok.lib.common.Uuids.uuid

@RunWith(SpringRunner.class)
@SpringBootTest(classes = [MongoAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class,
        MongodbDocumentStoreConfiguration.class])
class MongodbDocumentStoreConfigurationTest {

    def collection = uuid()

    def invoice = new Invoice(invoiceId: 'foo')

    def mapper = new ObjectMapper()

    @Autowired
    DocumentStore documentStore

    @BeforeClass
    static void beforeClass() {
        setIntProperty("spring.data.mongodb.port", findAvailableTcpPort())
    }

    // Tests

    @Test
    void shouldCountInvoice() {
        // Given
        documentStore.save(collection, serialize(invoice))

        // When
        def count = documentStore.countByQuery(collection, QueryBuilder.queryBuilder())

        // Then
        assertThat(count).isEqualTo(1)
    }

    @Test
    public void shouldFindOne() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedInvoice = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedInvoice).isNotNull()
    }

    @Test
    public void shouldUpdateDocument() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))
        invoice.id = id
        invoice.invoiceId = 'newValue'

        // When
        documentStore.save(collection, serialize(invoice))

        // Then
        def updatedInvoice = documentStore.findOne(collection, id)
        assertThat(updatedInvoice.invoiceId).isEqualTo(invoice.invoiceId)
    }

    @Test
    void shouldReturnEmptyList() {
        // When
        def invoices = documentStore.findByQuery(collection, new QueryBuilder())

        // Then
        assertThat(invoices.size()).isEqualTo(0);
    }

//    @Test
//    public void shouldFindByQuery() {
//        // Given
////        new RestTemplate().postForLocation("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice))
//        def query = [query: new InvoiceQuery().invoiceId(invoice.invoiceId)]
//
//        // When
////        def invoices = payloadEncoding.decode(new RestTemplate().postForObject("http://localhost:${httpPort}/document/findByQuery/${collection}", payloadEncoding.encode(query), byte[].class))
//
//        // Then
//        assertEquals(1, invoices.size());
//        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
//    }
//
//    @Test
//    public void shouldFindAllByQuery() {
//        // Given
////        new RestTemplate().postForLocation("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice))
////        new RestTemplate().postForLocation("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice))
//
//        // When
////        def invoices = payloadEncoding.decode(new RestTemplate().postForObject("http://localhost:${httpPort}/document/findByQuery/${collection}", payloadEncoding.encode([query:[:]]), byte[].class))
//
//        // Then
//        assertEquals(2, invoices.size());
//    }
//
//    @Test
//    public void shouldNotFindByQuery() {
//        // Given
//        invoice.invoiceId = 'invoice001'
////        new RestTemplate().postForLocation("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice))
//        InvoiceQuery query = new InvoiceQuery().invoiceId("randomValue");
//
//        // When
////        def invoices = payloadEncoding.decode(new RestTemplate().postForObject("http://localhost:${httpPort}/document/findByQuery/${collection}", payloadEncoding.encode([query: query]), byte[].class))
//
//        // Then
//        assertEquals(0, invoices.size());
//    }

    // Helpers

    private Map<String, Object> serialize(Invoice invoice) {
        mapper.convertValue(invoice, Map.class)
    }


    // Class fixtures

    static class Invoice {

        String id

        Date timestamp = new Date()

        String invoiceId

        Address address

        static class Address {

            String street

        }

    }

    static class InvoiceQuery {

        private String invoiceId;

        private String invoiceIdContains;

        private String[] invoiceIdIn;

        private String[] invoiceIdNotIn;

        private Date timestampLessThan;

        private Date timestampGreaterThanEqual;

        private String address_street;

        public String getInvoiceId() {
            return invoiceId;
        }

        public void setInvoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
        }

        public InvoiceQuery invoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public String getInvoiceIdContains() {
            return invoiceIdContains;
        }

        public void setInvoiceIdContains(String invoiceIdLike) {
            this.invoiceIdContains = invoiceIdLike;
        }

        public String[] getInvoiceIdIn() {
            return invoiceIdIn;
        }

        public void setInvoiceIdIn(String[] invoiceIdIn) {
            this.invoiceIdIn = invoiceIdIn;
        }

        public InvoiceQuery invoiceIdIn(String... invoiceIdIn) {
            this.invoiceIdIn = invoiceIdIn;
            return this;
        }

        public String[] getInvoiceIdNotIn() {
            return invoiceIdNotIn;
        }

        public void setInvoiceIdNotIn(String[] invoiceIdNotIn) {
            this.invoiceIdNotIn = invoiceIdNotIn;
        }

        public InvoiceQuery invoiceIdNotIn(String... invoiceIdNotIn) {
            this.invoiceIdNotIn = invoiceIdNotIn;
            return this;
        }

        public Date getTimestampLessThan() {
            return timestampLessThan;
        }

        public void setTimestampLessThan(Date timestampLessThan) {
            this.timestampLessThan = timestampLessThan;
        }

        public Date getTimestampGreaterThanEqual() {
            return timestampGreaterThanEqual;
        }

        public void setTimestampGreaterThanEqual(Date timestampGreaterThanEqual) {
            this.timestampGreaterThanEqual = timestampGreaterThanEqual;
        }

        public String getAddress_street() {
            return address_street;
        }

        public void setAddress_street(String address_street) {
            this.address_street = address_street;
        }

        public InvoiceQuery address_street(String address_street) {
            this.setAddress_street(address_street);
            return this;
        }

    }

}
