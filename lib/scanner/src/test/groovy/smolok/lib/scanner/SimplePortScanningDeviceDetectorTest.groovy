package smolok.lib.scanner

import org.junit.Assert
import org.junit.Test
import smolok.lib.ssh.server.NoneCredentialsPasswordAuthenticator
import smolok.lib.ssh.server.SshServerBuilder

import static org.assertj.core.api.Assertions.assertThat

class SimplePortScanningDeviceDetectorTest extends Assert {

    def detector = new SimplePortScanningDeviceDetector();

    @Test
    void shouldReachDevice() throws IOException {
        def sshd = new SshServerBuilder().build().start()
        def address = detector.detectDevices(sshd.port()).first()
        def ssh = new Socket(address.address(), sshd.port())
        assertThat(ssh.isConnected()).isTrue()
        sshd.stop()
    }

    @Test
    void shouldRejectNotAuthenticableDevice() {
        // Given
        def sshd = new SshServerBuilder().authenticator(new NoneCredentialsPasswordAuthenticator()).build().start()

        // When
        def devices = detector.detectDevices(sshd.port())

        // Then
        assertThat(devices).isEmpty()
        sshd.stop()
    }

}