package net.smolok.service.machinelearning.spark.spring

import net.smolok.service.binding.client.ServiceBindingClientFactory
import net.smolok.service.binding.client.ServiceBindingClientProxy
import net.smolok.service.machinelearning.api.FeatureVector
import net.smolok.service.machinelearning.api.MachineLearningService
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import smolok.eventbus.client.EventBus

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Uuids.uuid

@RunWith(SpringRunner)
@SpringBootTest(classes = KapuaApplication)
class SparkMachineLearningServiceConfigurationTest {

    @Autowired
    EventBus eventBus

    MachineLearningService machineLearningService
    
    def collection = uuid()

    @Before
    void before() {
        machineLearningService = new ServiceBindingClientFactory(eventBus).build(MachineLearningService, 'machinelearning')
    }

    // Tests

    @Test
    void shouldDetectSimilarity() {
        // Given
        machineLearningService.storeTrainingData(collection,
                new FeatureVector(text: 'Hi I heard about Spark', targetFeature: 0),
                new FeatureVector(text: 'I wish Java could use case classes', targetFeature: 0),
                new FeatureVector(text: 'Logistic regression models are neat', targetFeature: 1),
                new FeatureVector(text: 'Logistic regression models are neat', targetFeature: 1),
                new FeatureVector(text: 'Logistic regression models are neat', targetFeature: 1),
                new FeatureVector(text: 'Logistic regression models are neat', targetFeature: 1),
                new FeatureVector(text: 'Logistic regression models are neat', targetFeature: 1)
        )

        // When
        def result = machineLearningService.predict(collection, new FeatureVector(text: 'I love Logistic regression'))

        // Then
        assertThat(result['default']).isGreaterThan(0.4d)
    }

    @Test
    void shouldDetectDoubleSimilarity() {
        // Given
        machineLearningService.storeTrainingData(collection,
                new FeatureVector(text: 'Hi I heard about Spark', targetFeature: 0, targetLabel: 'foo'),
                 new FeatureVector(text: 'I wish Java could use case classes', targetFeature: 0, targetLabel: 'foo'),
                 new FeatureVector(text: 'Hi I heard about Spark', targetFeature: 0, targetLabel: 'lorem'),
                 new FeatureVector(text: 'I wish Java could use case classes', targetFeature: 0, targetLabel: 'lorem'),
                 new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                 new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                 new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                 new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                 new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                 new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                 new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                 new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                 new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                 new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem')
        )

        // When
        def result = machineLearningService.predict(collection, new FeatureVector(text: 'This text contains some foo and lorem'))

        // Then
        assertThat(result['foo']).isGreaterThan(0.7d)
        assertThat(result['lorem']).isGreaterThan(0.7d)
    }


}