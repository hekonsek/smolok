package net.smolok.service.device.kapua.spring

import net.smolok.service.device.kapua.KapuaDeviceService
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import smolok.bootstrap.Smolok

import static java.lang.System.setProperty
import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort

@RunWith(SpringRunner)
@SpringBootTest(classes = Smolok.class)
class KapuaDeviceServiceConfigurationTest {

    @Autowired
    KapuaDeviceService deviceService

    @BeforeClass
    static void beforeClass() {
        setProperty("spring.data.mongodb", findAvailableTcpPort() + "");
    }

    @Test
    void shouldInjectDeviceService() {
        assertThat(deviceService).isNotNull()
    }

}
