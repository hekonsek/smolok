package net.smolok.lib.machinelearning.spring

import net.smolok.lib.machinelearning.MulticlassTrainingFeatureVector
import net.smolok.lib.machinelearning.PredictionEngine
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import smolok.lib.common.Uuids

import static net.smolok.lib.machinelearning.IdentifiableFeatureVector.identifiableVector
import static net.smolok.lib.machinelearning.BinaryTrainingFeatureVector.matching
import static net.smolok.lib.machinelearning.BinaryTrainingFeatureVector.notMatching
import static net.smolok.lib.machinelearning.MulticlassTrainingFeatureVector.trainingFeatureVector
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Uuids.uuid

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PredictionEngineConfiguration.class)
class PredictionEngineConfigurationTest {

    @Autowired
    PredictionEngine predictionEngine

    def vectorId = uuid()

    @Test
    void shouldReturnPredictionForGivenId() {
        predictionEngine.train('LogisticRegression', [notMatching(5.0, 1.0),
                                                      notMatching(5, 5),
                                                      matching(10, 5),
                                                      matching(30, 1)])
        def predictions = predictionEngine.predict([identifiableVector('xxx', 9, 1)])
        assertThat(predictions.first().id()).isEqualTo('xxx')
    }

    @Test
    void shouldReturnPrediction() {
        predictionEngine.train('LogisticRegression', [notMatching(5.0, 1.0),
                                                      notMatching(5, 5),
                                                      matching(10, 5),
                                                      matching(30, 1)])
        def predictions = predictionEngine.predict([identifiableVector('xxx', 9, 1)])
        assertThat(predictions.first().binaryResult()).isEqualTo(true)
    }

    @Test
    void shouldReturnNaiveBayesPrediction() {
        predictionEngine.train('NaiveBayes', [trainingFeatureVector(0, 1, 5),
                                              trainingFeatureVector(0, 2, 7),
                                              trainingFeatureVector(1, 6, 2),
                                              trainingFeatureVector(1, 5, 3),
                                              trainingFeatureVector(1, 6, 1)])
        def predictions = predictionEngine.predict([identifiableVector(vectorId, 5, 1)])
        assertThat(predictions.first().result()).isEqualTo(1d)
    }

    @Test
    void shouldReturnMulticlassLogisticRegressionPrediction() {
        // Given
        def ageClassificationTrainingData = [
                // [age, hours of daily housekeeping, hours of daily Playstation playing]
                trainingFeatureVector(8, 1, 5),
                trainingFeatureVector(10, 2, 7),
                trainingFeatureVector(20, 6, 2),
                trainingFeatureVector(30, 5, 3),
                trainingFeatureVector(40, 6, 1)]
        predictionEngine.train(ageClassificationTrainingData)

        // When
        def predictions = predictionEngine.predict([identifiableVector(vectorId, 3, 4)])

        // Then
        assertThat(predictions.first().result()).isEqualTo(30.0d)
    }

}
