package smolok.lib.ingester.cassandra

import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.lib.docker.CommandLineDocker
import smolok.lib.docker.ContainerBuilder
import smolok.lib.ingester.Ingester
import smolok.lib.ingester.cassandra.spring.CassandraConfiguration
import smolok.lib.process.DefaultProcessManager

import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = [CassandraConfiguration.class])
class CassandraIngesterSinkTest {

    @Autowired
    CassandraIngesterSink cassandraIngesterSink

    @Autowired
    Cassandra cassandra

    @BeforeClass
    static void beforeClass() {
        new CommandLineDocker(new DefaultProcessManager()).startService(new ContainerBuilder('cassandra:3.7').name('cassandra').net('host').build())
        new Cassandra().tryStart()
    }

    def ingester = new Ingester('{"foo.bar": {"baz": "randomString(10)", "constant": 1000}}')

    @Test
    void shouldInjectRecordIntoCassandra() {
        ingester.ingest(cassandraIngesterSink, 1)
        def results = cassandra.session().execute("SELECT * FROM foo.bar")
        assertThat(results.all().size()).isGreaterThan(0)
    }

}
