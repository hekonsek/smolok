package net.smolok.paas

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class DeviceServiceImageLocatorResolverTest {

    def resolver = new DeviceServiceImageLocatorResolver()

    @Test
    void shouldResolveDeviceServiceImage() {
        def resolvedImage = resolver.resolveImage('device')
        assertThat(resolvedImage.last().image).startsWith('smolok/service-device')
    }

    @Test
    void shouldResolveMongodbServiceImage() {
        def resolvedImage = resolver.resolveImage('device')
        assertThat(resolvedImage.first().image).startsWith('mongo')
    }

}
