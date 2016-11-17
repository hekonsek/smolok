package net.smolok.paas

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class MachineLearningServiceImageLocatorResolverTest {

    def resolver = new MachineLearningServiceImageLocatorResolver()

    @Test
    void shouldResolveMachineLearningServiceImage() {
        def resolvedImage = resolver.resolveImage('machine-learning')
        assertThat(resolvedImage.last().image).startsWith('smolok/service-machinelearning')
    }

    @Test
    void shouldResolveSparkUser() {
        def resolvedImage = resolver.resolveImage('machine-learning')
        assertThat(resolvedImage.first().environment).containsEntry('SPARK_USER', 'root')
    }

}
