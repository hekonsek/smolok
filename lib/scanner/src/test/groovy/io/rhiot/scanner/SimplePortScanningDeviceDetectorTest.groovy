package io.rhiot.scanner

import org.junit.Assert
import org.junit.Ignore;
import org.junit.Test
import smolok.lib.ssh.server.NoneCredentialsPasswordAuthenticator
import smolok.lib.ssh.server.SshServerBuilder

import static com.google.common.truth.Truth.assertThat
import static java.lang.Boolean.parseBoolean
import static java.lang.System.getenv
import static org.junit.Assume.assumeFalse
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

public class SimplePortScanningDeviceDetectorTest extends Assert {

    static sshd = new SshServerBuilder().build().start()

    def detector = new SimplePortScanningDeviceDetector();

    @Test
    void shouldReachDevice() throws IOException {
        def address = detector.detectDevices(sshd.port()).first()
        def ssh = new Socket(address.address(), sshd.port())
        assertThat(ssh.isConnected()).isTrue()
    }

}