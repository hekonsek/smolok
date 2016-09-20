/**
 * Licensed to the Rhiot under one or more
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
import org.bson.types.ObjectId

import static MongodbMapper.mongoQuery
import static MongodbMapper.sortConditions
import static net.smolok.service.documentstore.mongodb.MongodbMapper.*
import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.Lang.doWith
import static smolok.lib.common.Lang.nullOr

class MongodbDocumentStore implements DocumentStore {

    // Logger

    private static final LOG = getLogger(MongodbDocumentStore.class)

    // Collaborators

    private final Mongo mongo

    // Configuration members

    private final String documentsDbName

    // Constructors

    MongodbDocumentStore(Mongo mongo, String documentsDbName) {
        this.mongo = mongo
        this.documentsDbName = documentsDbName
    }

    // Operations implementations

    @Override
    String save(String collection, Map<String, Object> pojo) {
        LOG.debug('About to save {} into {}.', pojo, collection)
        doWith(canonicalToMongo(pojo)) { documentCollection(collection).save(it) }[MONGO_ID].toString()
    }

    @Override
    Map<String, Object> findOne(String collection, String documentId) {
        nullOr(documentCollection(collection).findOne(new ObjectId(documentId))) { mongoToCanonical(it) }
    }

    @Override
    List<Map<String, Object>> findMany(String collection, List<String> ids) {
        def mongoIds = new BasicDBObject('$in', ids.collect{new ObjectId(it)})
        def query = new BasicDBObject('_id', mongoIds)
        documentCollection(collection).find(query).toArray().collect { mongoToCanonical(it) }
    }

    @Override
    List<Map<String, Object>> find(String collectionx, net.smolok.service.documentstore.api.QueryBuilder queryBuilder) {
        DBObject mongoQuery = mongoQuery(queryBuilder.query);
        int skip = queryBuilder.page * queryBuilder.size
        DBCursor results = documentCollection(collectionx).find(mongoQuery).
                limit(queryBuilder.size).skip(skip).sort(sortConditions(queryBuilder));
        results.toArray().collect{ mongoToCanonical(it) }
    }

    @Override
    long count(String collectionx, net.smolok.service.documentstore.api.QueryBuilder queryBuilder) {
        DBObject mongoQuery = mongoQuery(queryBuilder.query)
        int skip = queryBuilder.page * queryBuilder.size
        DBCursor results = documentCollection(collectionx).find(mongoQuery).limit(queryBuilder.size).skip(skip).sort(sortConditions(queryBuilder));
        return results.count();
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
