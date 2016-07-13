package smolok.lib.ssh.server

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory
import smolok.lib.ssh.client.SshClient
import org.apache.sshd.server.SshServer as ApacheSshServer

import java.nio.file.Paths

import static java.io.File.createTempFile

/**
 * Plugable SSH server which be used to expose Java services via SSH.
 */
class SshServer {

    // Collaborators

    private final PasswordAuthenticator authenticator

    // Internal collaborators

    private ApacheSshServer internalServer

    // Configuration

    private final int port

    private final File root

    // Constructors

    SshServer(PasswordAuthenticator authenticator, int port, File root) {
        this.authenticator = authenticator
        this.port = port
        this.root = root
    }

    // Life-cycle

    SshServer start() {
        internalServer = ApacheSshServer.setUpDefaultServer()
        internalServer.port = port
        internalServer.keyPairProvider = new SimpleGeneratorHostKeyProvider(createTempFile('smolok', 'host_keys'))
        internalServer.setPasswordAuthenticator(authenticator)
        internalServer.setCommandFactory(new EchoCommandFactory())
        internalServer.setFileSystemFactory(new VirtualFileSystemFactory(root.toPath()))
        internalServer.setSubsystemFactories([new SftpSubsystemFactory()])
        internalServer.start()

        this
    }

    void stop() {
        internalServer.stop()
    }

    // Factory methods

    SshClient client(String username, String password) {
        new SshClient('localhost', port, username, password)
    }

    Properties config(String path) {
        def properties = new Properties()
        def absolutePath = new File(root, path).absolutePath
        properties.load(new FileInputStream(Paths.get(absolutePath).toFile()))
        properties
    }

    // Accessors

    int port() {
        port
    }

    File root() {
        root
    }

}