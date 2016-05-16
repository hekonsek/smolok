package smolok.paas.openshift.spring

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.paas.Paas

import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenshiftPaasConfiguration.class)
class OpenshiftPaasConfigurationTest {

    // Collaborators fixtures

    @Autowired
    Paas paas

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
