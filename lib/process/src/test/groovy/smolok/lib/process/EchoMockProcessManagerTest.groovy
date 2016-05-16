package smolok.lib.process

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class EchoMockProcessManagerTest {

    def processManager = new EchoMockProcessManager()

    // Tests

    @Test
    void shouldReturnEcho() {
        def output = processManager.execute('foo')
        assertThat(output).isEqualTo(['foo'])
    }

    @Test
    void shouldReturnEchoAsynchronously() {
        def output = processManager.executeAsync('foo').get()
        assertThat(output).isEqualTo(['foo'])
    }

}
