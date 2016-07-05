package smolok.service.sparkjob.spring

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.bootstrap.Smolok
import smolok.eventbus.client.EventBus
import smolok.lib.spark.EchoSparkSubmit
import smolok.lib.spark.SparkSubmit
import smolok.lib.spark.SparkSubmitResult
import smolok.paas.Paas

import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = [Smolok.class])
@Configuration
class SparkJobServiceConfigurationTest {

    @Autowired
    EventBus eventBus

    @Bean
    SparkSubmit sparkSubmit() {
        new EchoSparkSubmit()
    }

    @Autowired
    Paas paas

    @Before
    void before() {
        paas.reset()
    }

    @Test
    void shouldCreateJob() {
        // Given
        def jobUri = 'jobid:file:path/job.jar'

        // When
        eventBus.toBusAndWait('spark-job.createJob', jobUri)
        def fetchedJobUri = eventBus.fromBus('spark-job.jobUri', 'jobid', String.class)

        // Then
        assertThat(fetchedJobUri).isEqualTo(jobUri)
    }

    @Test
    void shouldExecuteJob() {
        // Given
        def jobUri = 'jobid:file:path/job.jar'
        eventBus.toBusAndWait('spark-job.createJob', jobUri)

        // When
        def jobOutput = eventBus.fromBus('spark-job.executeJob', 'jobid', SparkSubmitResult.class)

        // Then
        assertThat(jobOutput.output).isEqualTo(['path/job.jar'])
    }

}
