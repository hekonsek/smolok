package smolok.lib.spark.job

import org.junit.BeforeClass
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.spark.job.RddSparkJob.OPTION_TESTING
import static smolok.lib.spark.job.RddSparkJob.enableTesting

class RddSparkJobTest {

    @BeforeClass
    static void beforeClass() {
        enableTesting()
    }

    // Tests

    @Test
    void shouldEnableTesting() {
        def testing = new RddSparkJob().option(OPTION_TESTING)
        assertThat(testing).is('true')
    }

    @Test
    void shouldLoadTestFile() {
        def job = new RddSparkJob()
        def count = job.source('text-file:/var/data/foo.txt').count()
        assertThat(count).isEqualTo(3)
    }

}
