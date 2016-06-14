package smolok.spark.archetype.rdd

import org.junit.Test
import smolok.lib.spark.SparkJobRequest

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.spark.SparkJobRequest.onRequest

class MySparkJobTest {

    @Test
    void shouldInjectSparkContext() {
        System.setProperty('spark.master', 'local[*]')

        onRequest { SparkJobRequest request ->
            assertThat(request.sparkContext).isNotNull()
        }
    }

}
