package smolok.service.sparkjob.spring

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.docker.Docker
import net.smolok.service.binding.ServiceBinding
import net.smolok.service.binding.ServiceEventProcessor
import smolok.service.sparkjob.DefaultSparkJobService
import smolok.lib.spark.DockerizedSparkSubmit
import smolok.service.sparkjob.InMemoryJobStore
import smolok.service.sparkjob.JobStore
import smolok.service.sparkjob.SparkJobService
import smolok.lib.spark.SparkSubmit

@Configuration
class SparkJobServiceConfiguration {

    @Bean
    ServiceBinding sparkJobServiceBinding(ServiceEventProcessor serviceEventProcessor) {
        new ServiceBinding(serviceEventProcessor, 'spark-job')
    }

    @Bean(name = 'spark-job')
    SparkJobService sparkJobService(JobStore jobStore, SparkSubmit sparkSubmit) {
        new DefaultSparkJobService(jobStore, sparkSubmit)
    }

    @Bean
    JobStore jobStore() {
        new InMemoryJobStore()
    }

    @Bean
    @ConditionalOnMissingBean
    SparkSubmit sparkSubmit(Docker docker) {
        new DockerizedSparkSubmit(docker)
    }

}