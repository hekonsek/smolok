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
        def count = documentStore.count(collection, QueryBuilder.queryBuilder())

        // Then
        assertThat(count).isEqualTo(1)
    }

    @Test
    void shouldFindOne() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedInvoice = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedInvoice).isNotNull()
    }

    @Test
    void loadedDocumentShouldHasId() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedInvoice = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedInvoice.id).isInstanceOf(String.class)
    }

    @Test
    void loadedDocumentShouldHasNotMongoId() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedInvoice = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedInvoice._id).isNull()
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
        def invoices = documentStore.find(collection, new QueryBuilder())

        // Then
        assertThat(invoices.size()).isEqualTo(0);
    }

    @Test
    public void shouldFindByQuery() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = [invoiceId: invoice.invoiceId]

        // When
        def invoices = documentStore.find(collection, new QueryBuilder(query))

        // Then
        assertThat(invoices.size()).isEqualTo(1)
        assertThat(invoices.first().invoiceId).isEqualTo(invoice.invoiceId)
    }

    @Test
    public void shouldFindAllByQuery() {
        // Given
        documentStore.save(collection, serialize(invoice))
        documentStore.save(collection, serialize(invoice))

        // When
        def invoices = documentStore.find(collection, new QueryBuilder())

        // Then
        assertThat(invoices).hasSize(2)
    }

    @Test
    public void shouldNotFindByQuery() {
        // Given
        documentStore.save(collection, serialize(invoice))

        invoice.invoiceId = 'invoice001'
        def query = new QueryBuilder([invoiceId: "randomValue"])

        // When
        def invoices = documentStore.find(collection, query)

        // Then
        assertThat(invoices).isEmpty()
    }

    @Test
    public void shouldFindMany() {
        // Given
        def firstInvoice = documentStore.save(collection, serialize(invoice))
        def secondInvoice = documentStore.save(collection, serialize(invoice))

        // When
        def invoices = documentStore.findMany(collection, [firstInvoice, secondInvoice])

        // Then
        assertThat(invoices).hasSize(2)
        assertThat(invoices.first().id).isEqualTo(firstInvoice)
        assertThat(invoices.last().id).isEqualTo(secondInvoice)
    }

    @Test
    public void shouldNotFindMany() {
        // When
        def invoices = documentStore.findMany(collection, [ObjectId.get().toString(), ObjectId.get().toString()])

        // Then
        assertThat(invoices).isEmpty()
    }

    @Test
    public void shouldNotFindOne() {
        // When
        def invoice = documentStore.findOne(collection, ObjectId.get().toString())

        // Then
        assertThat(invoice).isNull()
    }

    @Test
    public void shouldCount() {
        // Given
        documentStore.save(collection, serialize(invoice))

        // When
        long invoices = documentStore.count(collection, new QueryBuilder())

        // Then
        assertThat(invoices).isEqualTo(1)
    }


    @Test
    public void shouldFindByNestedQuery() {
        // Given
        String street = "someStreet";
        invoice.address = new Invoice.Address(street: street)
        documentStore.save(collection, serialize(invoice))

        def query = new QueryBuilder([address_street: street])

        // When
        def invoices = documentStore.find(collection, query);

        // Then
        assertThat(invoices).hasSize(1)
        assertThat(invoices.get(0).address.street).isEqualTo(street)
    }

    @Test
    public void shouldNotFindByNestedQuery() {
        // Given
        String street = "someStreet";
        invoice.address = new Invoice.Address(street: street)
        documentStore.save(collection, serialize(invoice))

        def query = new QueryBuilder([address_street: 'someRandomStreet'])

        // When
        def invoices = documentStore.find(collection, query);

        // Then
        assertThat(invoices).isEmpty()
    }

    @Test
    public void shouldReturnPageByQuery() {
        // Given
        def firstInvoice = documentStore.save(collection, serialize(invoice))
        def secondInvoice = documentStore.save(collection, serialize(invoice))
        def thirdInvoice = documentStore.save(collection, serialize(invoice))

        // When
        def firstPage = documentStore.find(collection, new QueryBuilder().page(0).size(2))
        def secondPage = documentStore.find(collection, new QueryBuilder().page(1).size(2))

        // Then
        assertThat(firstPage).hasSize(2)
        assertThat(secondPage).hasSize(1)
        assertThat(firstPage.get(0).id).isEqualTo(firstInvoice)
        assertThat(firstPage.get(1).id).isEqualTo(secondInvoice)
        assertThat(secondPage.get(0).id).isEqualTo(thirdInvoice)
    }

//    @Test
//    public void shouldSortDescending() {
//        // Given
//        Invoice firstInvoice = documentService.save(new Invoice().invoiceId("1"));
//        Invoice secondInvoice = documentService.save(new Invoice().invoiceId("2"));
//        Invoice thirdInvoice = documentService.save(new Invoice().invoiceId("3"));
//
//        // When
//        List<Invoice> firstPage = documentService.find(Invoice.class, buildQuery(
//                new InvoiceQuery()).size(2).orderBy("invoiceId").sortAscending(false).page(0));
//        List<Invoice> secondPage = documentService.find(Invoice.class, buildQuery(
//                new InvoiceQuery()).size(2).orderBy("invoiceId").sortAscending(false).page(1));
//
//        // Then
//        assertEquals(2, firstPage.size());
//        assertEquals(1, secondPage.size());
//        assertEquals(thirdInvoice.id, firstPage.get(0).id);
//        assertEquals(secondInvoice.id, firstPage.get(1).id);
//        assertEquals(firstInvoice.id, secondPage.get(0).id);
//    }
//
//    @Test
//    public void shouldFindByQueryWithContains() {
//        // Given
//        documentService.save(invoice);
//        InvoiceQuery query = new InvoiceQuery();
//        query.setInvoiceIdContains("voice");
//
//        // When
//        List<Invoice> invoices = documentService.find(Invoice.class, new QueryBuilder(query));
//
//        // Then
//        assertEquals(1, invoices.size());
//        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
//    }
//
//    @Test
//    public void shouldNotFindByQueryWithContains() {
//        // Given
//        documentService.save(invoice);
//        InvoiceQuery query = new InvoiceQuery();
//        query.setInvoiceIdContains("randomString");
//
//        // When
//        List<Invoice> invoices = documentService.find(Invoice.class, new QueryBuilder(query));
//
//        // Then
//        assertEquals(0, invoices.size());
//    }
//
//    @Test
//    public void shouldFindByQueryWithIn() {
//        // Given
//        documentService.save(invoice);
//        InvoiceQuery query = new InvoiceQuery().invoiceIdIn(invoice.invoiceId, "foo", "bar");
//
//        // When
//        List<Invoice> invoices = documentService.find(Invoice.class, new QueryBuilder(query));
//
//        // Then
//        assertEquals(1, invoices.size());
//        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
//    }
//
//    @Test
//    public void shouldNotFindByQueryWithIn() {
//        // Given
//        documentService.save(invoice);
//        InvoiceQuery query = new InvoiceQuery().invoiceIdIn("foo", "bar");
//
//        // When
//        List<Invoice> invoices = documentService.find(Invoice.class, new QueryBuilder(query));
//
//        // Then
//        assertEquals(0, invoices.size());
//    }
//
//    @Test
//    public void shouldFindByQueryWithNotIn() {
//        // Given
//        documentService.save(invoice);
//        InvoiceQuery query = new InvoiceQuery().invoiceIdNotIn("foo", "bar");
//
//        // When
//        List<Invoice> invoices = documentService.find(Invoice.class, new QueryBuilder(query));
//
//        // Then
//        assertEquals(1, invoices.size());
//        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
//    }
//
//    @Test
//    public void shouldNotFindByQueryWithNotIn() {
//        // Given
//        documentService.save(invoice);
//        InvoiceQuery query = new InvoiceQuery().invoiceIdNotIn(invoice.invoiceId, "foo", "bar");
//
//        // When
//        List<Invoice> invoices = documentService.find(Invoice.class, new QueryBuilder(query));
//
//        // Then
//        assertEquals(0, invoices.size());
//    }
//
//    @Test
//    public void shouldFindByQueryBetweenDateRange() {
//        // Given
//        Invoice todayInvoice = documentService.save(invoice);
//        invoice = new Invoice();
//        invoice.timestamp = now().minusDays(2).toDate();
//        documentService.save(invoice);
//        invoice = new Invoice();
//        invoice.timestamp = now().plusDays(2).toDate();
//        documentService.save(invoice);
//
//        InvoiceQuery query = new InvoiceQuery();
//        query.setTimestampGreaterThanEqual(now().minusDays(1).toDate());
//        query.setTimestampLessThan(now().plusDays(1).toDate());
//
//        // When
//        List<Invoice> invoices = documentService.find(Invoice.class, new QueryBuilder(query));
//
//        // Then
//        assertEquals(1, invoices.size());
//        assertEquals(todayInvoice.id, invoices.get(0).id);
//    }
//
//    @Test
//    public void shouldCountPositiveByQuery() {
//        // Given
//        documentService.save(invoice);
//        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.invoiceId);
//
//        // When
//        long invoices = documentService.count(Invoice.class, new QueryBuilder(query));
//
//        // Then
//        assertEquals(1, invoices);
//    }
//
//    @Test
//    public void shouldCountNegativeByQuery() {
//        // Given
//        documentService.save(invoice);
//        InvoiceQuery query = new InvoiceQuery().invoiceId("randomValue");
//
//        // When
//        long invoices = documentService.count(Invoice.class, new QueryBuilder(query));
//
//        // Then
//        assertEquals(0, invoices);
//    }
//
//    @Test
//    public void shouldCountPositiveByQueryWithContains() {
//        // Given
//        documentService.save(invoice);
//        InvoiceQuery query = new InvoiceQuery();
//        query.setInvoiceIdContains("voice");
//
//        // When
//        long invoices = documentService.count(Invoice.class, new QueryBuilder(query));
//
//        // Then
//        assertEquals(1, invoices);
//    }
//
//    @Test
//    public void shouldCountNegativeByQueryWithContains() {
//        // Given
//        documentService.save(invoice);
//        InvoiceQuery query = new InvoiceQuery();
//        query.setInvoiceIdContains("randomString");
//
//        // When
//        long invoices = documentService.count(Invoice.class, new QueryBuilder(query));
//
//        // Then
//        assertEquals(0, invoices);
//    }
//
//    @Test
//    public void shouldRemoveDocument() {
//        // Given
//        invoice = documentService.save(invoice);
//
//        // When
//        documentService.remove(Invoice.class, invoice.id);
//
//        // Then
//        long count = documentService.count(Invoice.class);
//        assertEquals(0, count);
//    }
//
//    @Test
//    public void shouldSetCustomApiEndpointOptions() {
//        for (Endpoint endpoint : camelContext.getEndpoints()) {
//            if (endpoint.getEndpointUri().startsWith("http")) {
//                NettyHttpEndpoint nettyEndpoint = (NettyHttpEndpoint) endpoint;
//                assertEquals(1000, nettyEndpoint.getConfiguration().getRequestTimeout());
//                assertEquals(20000, nettyEndpoint.getConfiguration().getConnectTimeout());
//                return;
//            }
//        }
//        fail("Can't find Netty endpoint with custom options.");
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

}