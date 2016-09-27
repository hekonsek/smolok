package smolok.paas

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class DeviceServiceImageLocatorResolverTest {

    def resolver = new DeviceServiceImageLocatorResolver()

    @Test
    void shouldResolveServiceImage() {
        def resolvedImage = resolver.resolveImage('device')
        assertThat(resolvedImage).startsWith('smolok/service-device')
    }

}
