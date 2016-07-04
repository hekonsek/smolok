package smolok.lib.ingester.cassandra

import com.datastax.driver.core.Session
import smolok.lib.ingester.Ingester
import smolok.lib.ingester.IngesterSink

class CassandraIngesterSink implements IngesterSink {

    Cassandra cassandra

    CassandraIngesterSink(Cassandra cassandra) {
        this.cassandra = cassandra
    }

    @Override
    void consume(Map<String, Object> record) {
        def session = cassandra.session()
        def table = record.keySet().first()
        def namespace = record.keySet().first().split(/\./)[0]
        def recordWithoutNamespace = record.values().first() as Map<String, Object>

        session.execute("CREATE KEYSPACE IF NOT EXISTS ${namespace} WITH replication " +
                "= {'class':'SimpleStrategy', 'replication_factor':3};")

        def keys = recordWithoutNamespace.keySet().toList()
        def keyTypes = [:]
        for(String key : keys) {
            if(recordWithoutNamespace[key].class == int || recordWithoutNamespace[key] instanceof Integer) {
                keyTypes[key] = 'int'
            } else {
                keyTypes[key] = 'text'
            }
        }

        if(!recordWithoutNamespace.containsKey('id')) {
            recordWithoutNamespace.id = UUID.randomUUID().toString()
            keys << 'id'
            keyTypes.id = 'uuid PRIMARY KEY'
        }

        session.execute(
                "CREATE TABLE IF NOT EXISTS ${table} (" +
                        keys.collect{ "${it} ${keyTypes[it]}"}.join(', ') +
                        ");");

        def values = []
        for(String key : keys) {
            if(keyTypes[key] == 'text') {
                values << "'${recordWithoutNamespace[key]}'"
            } else {
                values << recordWithoutNamespace[key]
            }
        }
        session.execute(
                "INSERT INTO ${table} (${keys.join(',')}) " +
                        "VALUES (${values.join(', ')});")
    }

}