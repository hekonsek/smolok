package net.smolok.lib.machinelearning.spring

import net.smolok.lib.machinelearning.PredictionEngine
import org.apache.spark.sql.SparkSession
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PredictionEngineConfiguration {

    @Bean
    PredictionEngine predictionEngine(SparkSession sparkSession) {
        new PredictionEngine(sparkSession)
    }

    @Bean
    SparkSession sparkSession() {
        SparkSession.builder()
                .master("local[*]")
                .getOrCreate()
    }

}
