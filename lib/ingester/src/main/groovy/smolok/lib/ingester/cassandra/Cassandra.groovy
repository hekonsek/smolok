package smolok.lib.ingester.cassandra

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session

class Cassandra {

    private final Cluster cluster

    Cassandra() {
        cluster = Cluster.builder().addContactPoint('127.0.0.1').build()
    }

    void close() {
        cluster.close()
    }

    Session session() {
        cluster.connect()
    }

}
