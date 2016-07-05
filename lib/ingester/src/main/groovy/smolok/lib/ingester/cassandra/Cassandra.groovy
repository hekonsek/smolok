package smolok.lib.ingester.cassandra

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import com.jayway.awaitility.Awaitility

import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

import static java.util.concurrent.TimeUnit.MINUTES

class Cassandra {

    private Cluster cluster

    void start() {
        cluster = Cluster.builder().addContactPoint('127.0.0.1').build()
    }

    void tryStart() {
        Awaitility.await().atMost(2, MINUTES).until({
            try {
                start()
                true
            } catch (Exception e) {
                false
            }
        } as Callable<Boolean>)
    }

    void stop() {
        cluster.close()
    }

    Session session() {
        cluster.connect()
    }

}
