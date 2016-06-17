package smolok.lib.ssh.server

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory
import smolok.lib.ssh.client.SshClient

import java.nio.file.Paths

import static java.io.File.createTempFile

class SshServer {

    private final PasswordAuthenticator authenticator

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
        def sshd = org.apache.sshd.server.SshServer.setUpDefaultServer()
        sshd.setPort(port)
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(createTempFile('smolok', 'host_keys')))
        sshd.setPasswordAuthenticator(authenticator)
        sshd.setCommandFactory(new EchoCommandFactory())
        sshd.setFileSystemFactory(new VirtualFileSystemFactory(root.toPath()))
        sshd.setSubsystemFactories([new SftpSubsystemFactory()])
        sshd.start()

        this
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