package smolok.lib.ssh.server

import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.session.ServerSession

class AnyCredentialsPasswordAuthenticator implements PasswordAuthenticator {

    @Override
    boolean authenticate(String username, String password, ServerSession session) {
        true
    }

}
