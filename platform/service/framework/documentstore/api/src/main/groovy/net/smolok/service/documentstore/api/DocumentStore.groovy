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
package net.smolok.service.documentstore.api
/**
 * Document stores can be used to perform CRUD operations on messages formatted as hierarchical documents. All the data
 * structures as as arguments for operations and returned by the operations are supposed to be easily serialized into
 * hierarchical data structures.
 */
interface DocumentStore {

    /**
     * Persists document into a store. If document doesn't contain ID, the former will be considered a new document and
     * new ID will be assigned to it during creation process. Documents with ID are updated.
     *
     * @param collection name of the target collection
     * @param document document to be persisted
     * @return ID assigned to the persisted object
     */
    String save(String collection, Map<String, Object> document)

    Map<String, Object> findOne(String collection, String id)

    List<Map<String, Object>> findMany(String collection, List<String> ids)

    List<Map<String,Object>> find(String collection, QueryBuilder queryBuilder)

    /**
     * Return total number of documents in the given collection.
     * @param collection to count against.
     * @return
     */
    long count(String collection)

    /**
     * Returns the number of the documents in the given collection.
     *
     * @param collection to query against.
     * @param queryBuilder defined filtering criteria for the counting process
     * @return number of documents. Returns 0 for non-existing collections as well.
     */
    long count(String collection, QueryBuilder queryBuilder)

    void remove(String collection, String id)

    /**
     * Returns a list of distinct values in a given column of a given collection
     * @param collection to query against
     * @param column with distinct values
     * @return The distinct values from the column
     */
    List<String> distinct(String collection, String column)

}
