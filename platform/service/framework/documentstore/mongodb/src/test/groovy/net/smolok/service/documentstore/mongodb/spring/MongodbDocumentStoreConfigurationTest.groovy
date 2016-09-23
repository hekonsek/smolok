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
import org.joda.time.DateTime
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
        MongodbDocumentStoreConfiguration.class])
class MongodbDocumentStoreConfigurationTest {

    @Autowired
    DocumentStore documentStore

    def mapper = new ObjectMapper()

    // Data fixtures

    def collection = uuid()

    def invoice = new Invoice(invoiceId: 'foo')

    // Store configuration fixtures

    @BeforeClass
    static void beforeClass() {
        setSystemStringProperty('documentStore.mongodb.idField', 'myid')
        setIntProperty("spring.data.mongodb.port", findAvailableTcpPort())
    }

    // FindOne tests

    @Test
    void shouldFindOne() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedDocument = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedDocument).isNotNull()
    }

    @Test
    void shouldNotFindOne() {
        // Given
        def randomId = ObjectId.get().toString()

        // When
        def invoice = documentStore.findOne(collection, randomId)

        // Then
        assertThat(invoice).isNull()
    }

    @Test
    void findOneShouldContainsID() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedDocument = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedDocument.myid).isEqualTo(id)
    }

    @Test
    void findOneShouldContainMongoID() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedDocument = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedDocument._id).isNull()
    }

    @Test(expected = NullPointerException.class)
    void findOneShouldValidateNullId() {
        documentStore.findOne(collection, null)
    }

    @Test(expected = NullPointerException.class)
    void findOneShouldValidateNullCollection() {
        documentStore.findOne(null, 'someId')
    }

    // Other tests

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
    void loadedDocumentShouldHasId() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedInvoice = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedInvoice.myid).isInstanceOf(String.class)
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
        invoice.myid = id
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
    void shouldFindOneByQuery() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def invoices = documentStore.find(collection, new QueryBuilder([myid: id]))

        // Then
        assertThat(invoices).isNotEmpty()
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
        assertThat(invoices.first().myid).isEqualTo(firstInvoice)
        assertThat(invoices.last().myid).isEqualTo(secondInvoice)
    }

    @Test
    public void shouldNotFindMany() {
        // When
        def invoices = documentStore.findMany(collection, [ObjectId.get().toString(), ObjectId.get().toString()])

        // Then
        assertThat(invoices).isEmpty()
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
    void shouldReturnPageByQuery() {
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
        assertThat(firstPage.get(0).myid).isEqualTo(firstInvoice)
        assertThat(firstPage.get(1).myid).isEqualTo(secondInvoice)
        assertThat(secondPage.get(0).myid).isEqualTo(thirdInvoice)
    }

    @Test
    void shouldSortDescending() {
        // Given
        def firstInvoice = documentStore.save(collection, serialize(invoice))
        def secondInvoice = documentStore.save(collection, serialize(invoice))
        def thirdInvoice = documentStore.save(collection, serialize(invoice))

        // When
        def firstPage = documentStore.find(collection, new QueryBuilder().page(0).size(2).sortAscending(false).orderBy('myid'))
        def secondPage = documentStore.find(collection, new QueryBuilder().page(1).size(2).sortAscending(false).orderBy('myid'))

        // Then
        assertThat(firstPage).hasSize(2)
        assertThat(secondPage).hasSize(1)
        assertThat(firstPage.get(0).myid).isEqualTo(thirdInvoice)
        assertThat(firstPage.get(1).myid).isEqualTo(secondInvoice)
        assertThat(secondPage.get(0).myid).isEqualTo(firstInvoice)
    }

    @Test
    public void shouldFindByQueryWithContains() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = new QueryBuilder([invoiceIdContains: 'oo'])

        // When
        def invoices = documentStore.find(collection, query);

        // Then
        assertThat(invoices).hasSize(1);
        assertThat(invoices.get(0).invoiceId).isEqualTo(invoice.invoiceId)
    }

    @Test
    public void shouldNotFindByQueryWithContains() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = new QueryBuilder([invoiceIdContains: 'randomString'])

        // When
        def invoices = documentStore.find(collection, query);

        // Then
        assertThat(invoices).isEmpty()
    }

    @Test
    void shouldFindByQueryWithIn() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = new QueryBuilder([invoiceIdIn: ['foo', 'bar']])

        // When
        def invoices = documentStore.find(collection, query);

        // Then
        assertThat(invoices).hasSize(1);
        assertThat(invoices.get(0).invoiceId).isEqualTo(invoice.invoiceId)
    }

    @Test
    void shouldNotFindByQueryWithIn() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = new QueryBuilder([invoiceIdIn: ['baz', 'bar']])

        // When
        def invoices = documentStore.find(collection, query);

        // Then
        assertThat(invoices).hasSize(0);
    }

    @Test
    void shouldFindByQueryWithNotIn() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = new QueryBuilder([invoiceIdNotIn: ['baz', 'bar']])

        // When
        def invoices = documentStore.find(collection, query);

        // Then
        assertThat(invoices).hasSize(1);
        assertThat(invoices.get(0).invoiceId).isEqualTo(invoice.invoiceId)
    }

    @Test
    void shouldNotFindByQueryWithNotIn() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = new QueryBuilder([invoiceIdNotIn: ['foo', 'bar']])

        // When
        def invoices = documentStore.find(collection, query);

        // Then
        assertThat(invoices).isEmpty()
    }

    @Test
    void shouldFindByQueryBetweenDateRange() {
        // Given
        invoice.timestamp = now().toDate()
        def todayInvoice = documentStore.save(collection, serialize(invoice))
        invoice = new Invoice()
        invoice.timestamp = now().minusDays(2).toDate();
        documentStore.save(collection, serialize(invoice))
        invoice = new Invoice();
        invoice.timestamp = now().plusDays(2).toDate()
        documentStore.save(collection, serialize(invoice));

        def query = new QueryBuilder([timestampGreaterThanEqual: now().minusDays(1).toDate().time, timestampLessThan: now().plusDays(1).toDate().time])

        // When
        def invoices = documentStore.find(collection, query)

        // Then
        assertThat(invoices).hasSize(1);
        assertThat(invoices.first().myid).isEqualTo(todayInvoice)
    }

    @Test
    void shouldCountPositiveByQuery() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = new QueryBuilder([invoiceId: 'foo'])

        // When
        long invoices = documentStore.count(collection, query);

        // Then
        assertThat(invoices).isEqualTo(1);
    }

    @Test
    public void shouldCountNegativeByQuery() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = new QueryBuilder([invoiceId: 'randomValue'])

        // When
        long invoices = documentStore.count(collection, query);

        // Then
        assertThat(invoices).isEqualTo(0)
    }

    @Test
    public void shouldCountPositiveByQueryWithContains() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = new QueryBuilder([invoiceIdContains: 'oo'])

        // When
        long invoices = documentStore.count(collection, query);

        // Then
        assertThat(invoices).isEqualTo(1);
    }

    @Test
    public void shouldCountNegativeByQueryWithContains() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = new QueryBuilder([invoiceIdContains: 'invalidQuery'])

        // When
        long invoices = documentStore.count(collection, query);

        // Then
        assertThat(invoices).isEqualTo(0)
    }

    @Test
    public void shouldRemoveDocument() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        documentStore.remove(collection, id);

        // Then
        long invoices = documentStore.count(collection, new QueryBuilder());
        assertThat(invoices).isEqualTo(0)
    }

    // Helpers

    private Map<String, Object> serialize(Invoice invoice) {
        mapper.convertValue(invoice, Map.class)
    }


    // Class fixtures

    static class Invoice {

        String myid

        Date timestamp = new Date()

        String invoiceId

        Address address

        static class Address {

            String street

        }

    }

}