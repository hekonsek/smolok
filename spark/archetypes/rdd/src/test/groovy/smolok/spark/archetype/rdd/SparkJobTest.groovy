package smolok.spark.archetype.rdd

import org.apache.spark.api.java.JavaSparkContext
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SparkJob.class)
@IntegrationTest('spark.master=local[*]')
class SparkJobTest {

    @Autowired
    JavaSparkContext sparkContext

    @Test
    void shouldInjectSparkContext() {
        assertThat(sparkContext).isNotNull()
    }

}
