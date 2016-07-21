package smolok.spark.archetype.rdd

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.spark.job.RddJobContext.enableTesting

class MySparkJobTest {

    @Test
    void shouldInjectSparkContext() {
        enableTesting()
        def context = MySparkJob.job.sparkContext()
        assertThat(context).isNotNull()
    }

}
