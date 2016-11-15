package net.smolok.service.machinelearning.spark.spring

import net.smolok.service.machinelearning.api.MachineLearningService
import net.smolok.service.machinelearning.spark.SparkMachineLearningService
import org.apache.spark.sql.SparkSession
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SparkMachineLearningServiceConfiguration {

    @Bean
    MachineLearningService machineLearningService(SparkSession spark) {
        new SparkMachineLearningService(spark)
    }

}
