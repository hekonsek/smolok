package smolok.paas.openshift

import org.junit.Before
import org.junit.Test
import smolok.lib.process.DefaultProcessManager
import smolok.paas.openshift.OpenshiftPaas

import static org.assertj.core.api.Assertions.assertThat

class OpenshiftPaasTest {

    // Collaborators fixtures

    def paas = new OpenshiftPaas(new DefaultProcessManager())

    @Before
    void before() {
        paas.reset()
    }

    // Tests

    @Test
    void shouldStart() {
        // When
        paas.start()

        // Then
        assertThat(paas.started).isTrue()
    }

    @Test
    void shouldProvisionAfterStart() {
        // When
        paas.start()

        // Then
        assertThat(paas.provisioned).isTrue()
    }

    @Test
    void shouldStop() {
        // Given
        paas.start()

        // When
        paas.stop()

        // Then
        assertThat(paas.started).isFalse()
    }

    @Test
    void shouldStopAfterReset() {
        // Given
        paas.start()

        // When
        paas.reset()

        // Then
        assertThat(paas.started).isFalse()
    }

}
