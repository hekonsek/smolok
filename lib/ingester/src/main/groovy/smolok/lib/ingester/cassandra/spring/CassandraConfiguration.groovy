package smolok.lib.ingester.cassandra.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.cassandra.Cassandra
import smolok.lib.ingester.cassandra.CassandraIngesterSink

@Configuration
class CassandraConfiguration {

    @Bean(initMethod = 'start', destroyMethod = 'stop')
    Cassandra cassandra() {
        new Cassandra()
    }

    @Bean
    CassandraIngesterSink cassandraIngesterSink(Cassandra cassandra) {
        new CassandraIngesterSink(cassandra)
    }

}
