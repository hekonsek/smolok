/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.smolok.service.documentstore.mongodb

import com.mongodb.*
import net.smolok.service.documentstore.api.DocumentStore
import org.apache.commons.lang3.Validate
import org.bson.types.ObjectId

import static MongodbMapper.sortConditions
import static net.smolok.service.documentstore.mongodb.MongodbMapper.*
import static net.smolok.service.documentstore.mongodb.MongodbMapper.mongoQuery
import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.Lang.doWith
import static smolok.lib.common.Lang.nullOr

class MongodbDocumentStore implements DocumentStore {

    // Logger

    private static final LOG = getLogger(MongodbDocumentStore.class)

    // Collaborators

    private final Mongo mongo

    private final MongodbMapper mongodbMapper

    // Configuration members

    private final String documentsDbName

    // Constructors

    MongodbDocumentStore(Mongo mongo, MongodbMapper mongodbMapper, String documentsDbName) {
        this.mongo = Validate.notNull(mongo, 'Mongo client expected not to be null.')
        this.mongodbMapper = Validate.notNull(mongodbMapper, 'MongoDB mapper expected not to be null.')
        this.documentsDbName = Validate.notBlank(documentsDbName, 'Documents database name expected not to be blank.')
    }

    // Operations implementations

    @Override
    String save(String collection, Map<String, Object> pojo) {
        LOG.debug('About to save {} into {}.', pojo, collection)
        doWith(mongodbMapper.canonicalToMongo(pojo)) { documentCollection(collection).save(it) }[MONGO_ID].toString()
    }

    @Override
    Map<String, Object> findOne(String collection, String documentId) {
        Validate.notNull(documentId, 'Document ID expected not to be null.')
        Validate.notNull(collection, 'Document collection expected not to be null.')

        LOG.debug('Looking up for document with ID {} from collection {}.', documentId, collection)
        nullOr(documentCollection(collection).findOne(new ObjectId(documentId))) { mongodbMapper.mongoToCanonical(it) }
    }

    @Override
    List<Map<String, Object>> findMany(String collection, List<String> ids) {
        def mongoIds = new BasicDBObject('$in', ids.collect{new ObjectId(it)})
        def query = new BasicDBObject(MONGO_ID, mongoIds)
        documentCollection(collection).find(query).toArray().collect { mongodbMapper.mongoToCanonical(it) }
    }

    @Override
    List<Map<String, Object>> find(String collection, net.smolok.service.documentstore.api.QueryBuilder queryBuilder) {
        documentCollection(collection).find(mongodbMapper.mongoQuery(queryBuilder.query)).
                limit(queryBuilder.size).skip(queryBuilder.skip()).sort(mongodbMapper.sortConditions(queryBuilder)).
                toArray().collect{ mongodbMapper.mongoToCanonical(it) }
    }

    @Override
    long count(String collection) {
        documentCollection(collection).count()
    }

    @Override
    long count(String collection, net.smolok.service.documentstore.api.QueryBuilder queryBuilder) {
        documentCollection(collection).find(mongodbMapper.mongoQuery(queryBuilder.query)).
                limit(queryBuilder.size).skip(queryBuilder.skip()).sort(mongodbMapper.sortConditions(queryBuilder)).
                count()
    }

    @Override
    void remove(String collection, String identifier) {
        documentCollection(collection).remove(new BasicDBObject(MONGO_ID, new ObjectId(identifier)))
    }

    // Helpers

    private DBCollection documentCollection(String collection) {
        mongo.getDB(documentsDbName).getCollection(collection)
    }

}
