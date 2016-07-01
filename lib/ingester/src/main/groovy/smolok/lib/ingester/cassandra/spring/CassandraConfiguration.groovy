package smolok.lib.ingester.cassandra.spring

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.ingester.cassandra.Cassandra
import smolok.lib.ingester.cassandra.CassandraIngesterSink

@Configuration
class CassandraConfiguration {

    @Bean(destroyMethod = 'close')
    Cassandra cassandra() {
        new Cassandra()
    }

    @Bean
    CassandraIngesterSink cassandraIngesterSink(Cassandra cassandra) {
        new CassandraIngesterSink(cassandra)
    }

}
