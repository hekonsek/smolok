package smolok.lib.ssh.server

import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.session.ServerSession

class FixedCredentialsPasswordAuthenticator implements PasswordAuthenticator {

    private final String username

    private final String password

    FixedCredentialsPasswordAuthenticator(String username, String password) {
        this.username = username
        this.password = password
    }

    @Override
    boolean authenticate(String username, String password, ServerSession session) {
        username == this.username && password == this.password
    }

}