package smolok.lib.ssh.client

import smolok.lib.ssh.server.SshServerBuilder
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class SshClientTest {

    static sshd = new SshServerBuilder().build().start()

    static ssh = sshd.client('foo', 'bar')

    def file = new File("/parent/${UUID.randomUUID().toString()}")

    // Tests

    @Test
    void shouldCheckConnection() {
        ssh.checkConnection()
    }

    @Test
    void shouldExecuteCommand() {
        // Given
        def command = 'some command'

        // When
        def result = ssh.command(command)
        // Then
        assertThat(result).isEqualTo([command])
    }

    @Test
    void shouldHandleEmptyFile() {
        assertThat(ssh.scp(file)).isNull()
    }

    @Test
    void shouldSendAndReceiveFile() {
        // Given
        def text = 'foo'
        ssh.scp(new ByteArrayInputStream(text.getBytes()), file)

        // When
        def received = new String(ssh.scp(file))

        // Then
        assertThat(received).isEqualTo(text)
    }

}