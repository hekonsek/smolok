package smolok.lib.common

import org.junit.Assert
import org.junit.Test

import static java.lang.System.currentTimeMillis
import static java.util.concurrent.TimeUnit.SECONDS
import static org.assertj.core.api.Assertions.assertThat
import static org.junit.Assume.assumeTrue

class NetworksTest extends Assert {

    @Test
    void shouldReturnAvailablePort() {
        assertThat(Networks.findAvailableTcpPort()).isGreaterThan(Networks.MIN_PORT_NUMBER)
    }

    @Test
    void shouldReachHost() {
        assumeTrue('This test should be executed only if you can access rhiot.io from your network.',
                Networks.isReachable('rhiot.io', (int) SECONDS.toMillis(10)))
    }

    @Test
    void shouldNotReachHost() {
        assertThat(Networks.isReachable("someUnreachableHostName${currentTimeMillis()}")).isFalse()
    }

    @Test
    void shouldReturnCurrentLocalNetworkIp() {
        assertThat(Networks.currentLocalNetworkIp()).isNotNull()
    }

}
