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
package net.smolok.service.documentstore.mongodb

import com.google.common.base.Preconditions
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import net.smolok.service.documentstore.api.QueryBuilder
import org.bson.types.ObjectId

class MongodbMapper {

    static final def MONGO_ID = '_id'

    private static final SIMPLE_SUFFIX_OPERATORS = [
            "GreaterThan": '$gt',
            "GreaterThanEqual": '$gte',
            "LessThan": '$lt',
            "LessThanEqual": '$lte',
            "NotIn": '$nin',
            "In": '$in'];

    static DBObject mongoQuery(Map<String, Object> jsonQuery) {
        def mongoQuery = new BasicDBObject()
        for (String originalKey : jsonQuery.keySet()) {
            String compoundKey = originalKey.replaceAll('(.)_', '$1.');

            String suffixOperator = findFirstMatchOperator(originalKey);
            if (suffixOperator != null) {
                addRestriction(mongoQuery, compoundKey, suffixOperator, SIMPLE_SUFFIX_OPERATORS.get(suffixOperator), jsonQuery.get(originalKey));
                continue;
            }

            if (originalKey.endsWith("Contains")) {
                addRestriction(mongoQuery, compoundKey, "Contains", '$regex', ".*" + jsonQuery.get(originalKey) + ".*");
            } else {
                mongoQuery.put(compoundKey, new BasicDBObject('$eq', jsonQuery.get(originalKey)));
            }
        }
        mongoQuery
    }

    static DBObject sortConditions(QueryBuilder queryBuilder) {
        int order = queryBuilder.sortAscending ? 1 : -1
        def orderBy = queryBuilder.orderBy
        if (orderBy.size() == 0) {
            new BasicDBObject('$natural', order);
        } else {
            BasicDBObject sort = new BasicDBObject();
            for (String by : orderBy) {
                sort.put(by, order);
            }
            sort
        }
    }

    static DBObject canonicalToMongo(Map<String, Object> document) {
        Preconditions.checkNotNull(document, "JSON passed to the conversion can't be null.");

        def bson = new BasicDBObject(document)
        Object id = bson.get("id");
        if (id != null) {
            bson.removeField("id");
            bson.put("_id", new ObjectId(id.toString()));
        }
        return bson;
    }

    static Map<String, Object> mongoToCanonical(DBObject bson) {
        Preconditions.checkNotNull(bson, "BSON passed to the conversion can't be null.");
        def json = bson.toMap()
        Object id = json.get("_id");
        if (id != null) {
            json.remove("_id");
            json.put("id", id.toString());
        }
        return json;
    }

    // Helpers

    private static String findFirstMatchOperator(String originalKey) {
        List<String> matchingSuffixOperators = SIMPLE_SUFFIX_OPERATORS.keySet().findAll{originalKey.endsWith(it)}.toList()
        return matchingSuffixOperators.isEmpty() ? null : matchingSuffixOperators.get(0);
    }

    private static void addRestriction(BasicDBObject query, String propertyWithOperator, String propertyOperator, String operator, Object value) {
        String property = propertyWithOperator.replaceAll(propertyOperator + '$', "");
        if (query.containsField(property)) {
            BasicDBObject existingRestriction = (BasicDBObject) query.get(property);
            existingRestriction.put(operator, value);
        } else {
            query.put(property, new BasicDBObject(operator, value));
        }
    }

}
