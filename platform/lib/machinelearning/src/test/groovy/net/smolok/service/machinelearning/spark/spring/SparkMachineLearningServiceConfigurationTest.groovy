package net.smolok.service.machinelearning.spark.spring

import net.smolok.lib.machinelearning.spring.PredictionEngineConfiguration
import net.smolok.service.machinelearning.api.FeatureVector
import net.smolok.service.machinelearning.api.MachineLearningService
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner)
@SpringBootTest(classes = [PredictionEngineConfiguration, SparkMachineLearningServiceConfiguration])
class SparkMachineLearningServiceConfigurationTest {

    @Autowired
    MachineLearningService machineLearningService

    @Test
    void shouldDetectSimilarity() {
        // Given
        machineLearningService.storeTrainingData('collection', new FeatureVector(text: 'Hi I heard about Spark', targetFeature: 0))
        machineLearningService.storeTrainingData('collection', new FeatureVector(text: 'I wish Java could use case classes', targetFeature: 0))
        machineLearningService.storeTrainingData('collection', new FeatureVector(text: 'Logistic regression models are neat', targetFeature: 1))
        machineLearningService.storeTrainingData('collection', new FeatureVector(text: 'Logistic regression models are neat', targetFeature: 1))
        machineLearningService.storeTrainingData('collection', new FeatureVector(text: 'Logistic regression models are neat', targetFeature: 1))
        machineLearningService.storeTrainingData('collection', new FeatureVector(text: 'Logistic regression models are neat', targetFeature: 1))
        machineLearningService.storeTrainingData('collection', new FeatureVector(text: 'Logistic regression models are neat', targetFeature: 1))

        // When
        def result = machineLearningService.predict('collection', new FeatureVector(text: 'I love Logistic regression'))

        // Then
        Assertions.assertThat(result.first()).isGreaterThan(0.4d)
    }


}