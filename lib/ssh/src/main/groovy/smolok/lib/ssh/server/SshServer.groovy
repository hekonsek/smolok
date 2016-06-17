package smolok.lib.ssh.server

import smolok.lib.ssh.client.SshClient
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.server.PasswordAuthenticator
import org.apache.sshd.server.command.ScpCommandFactory
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.sftp.SftpSubsystem

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
        def sshd = org.apache.sshd.SshServer.setUpDefaultServer()
        sshd.setPort(port)
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(createTempFile('smolok', 'host_keys').absolutePath));
        sshd.setPasswordAuthenticator(authenticator)
        sshd.setCommandFactory(new ScpCommandFactory())
        sshd.setFileSystemFactory(new VirtualFileSystemFactory(root.absolutePath))
        sshd.setSubsystemFactories([new SftpSubsystem.Factory()])
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