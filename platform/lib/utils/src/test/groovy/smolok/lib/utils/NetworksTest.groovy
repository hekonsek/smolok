package smolok.lib.utils

import org.junit.Assert
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static smolok.lib.utils.Networks.findAvailableTcpPort
import static smolok.lib.utils.Networks.isReachable
import static smolok.lib.utils.Networks.currentLocalNetworkIp
import static java.lang.System.currentTimeMillis
import static java.util.concurrent.TimeUnit.SECONDS
import static org.junit.Assume.assumeTrue;

class NetworksTest extends Assert {

    @Test
    void shouldReturnAvailablePort() {
        assertThat(findAvailableTcpPort()).isGreaterThan(Networks.MIN_PORT_NUMBER)
    }

    @Test
    void shouldReachHost() {
        assumeTrue('This test should be executed only if you can access rhiot.io from your network.',
                isReachable('rhiot.io', (int) SECONDS.toMillis(10)))
    }

    @Test
    void shouldNotReachHost() {
        assertThat(isReachable("someUnreachableHostName${currentTimeMillis()}")).isFalse()
    }

    @Test
    void shouldReturnCurrentLocalNetworkIp() {
        assertThat(currentLocalNetworkIp()).isNotNull()
    }

}
