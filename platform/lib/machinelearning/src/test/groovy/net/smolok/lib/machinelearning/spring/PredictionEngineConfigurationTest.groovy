package net.smolok.lib.machinelearning.spring

import net.smolok.lib.machinelearning.PredictionEngine
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static net.smolok.lib.machinelearning.IdentifiableFeatureVector.identifiableVector
import static net.smolok.lib.machinelearning.TrainingFeatureVector.matching
import static net.smolok.lib.machinelearning.TrainingFeatureVector.notMatching

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PredictionEngineConfiguration.class)
class PredictionEngineConfigurationTest {

    @Autowired
    PredictionEngine predictionEngine

    @Test
    void shouldReturnPredictionForGivenId() {
        predictionEngine.train([notMatching(5.0, 1.0),
                              notMatching(5, 5),
                              matching(10, 5),
                              matching(30, 1)])
        def predictions = predictionEngine.predict([identifiableVector('xxx', 9, 1)])
        Assertions.assertThat(predictions.first().id()).isEqualTo('xxx')
    }

    @Test
    void shouldReturnPrediction() {
        predictionEngine.train([notMatching(5.0, 1.0),
                                notMatching(5, 5),
                                matching(10, 5),
                                matching(30, 1)])
        def predictions = predictionEngine.predict([identifiableVector('xxx', 9, 1)])
        Assertions.assertThat(predictions.first().result()).isEqualTo(true)
    }

}
