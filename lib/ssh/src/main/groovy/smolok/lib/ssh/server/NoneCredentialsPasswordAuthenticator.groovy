package smolok.lib.ssh.server

import org.apache.sshd.server.PasswordAuthenticator
import org.apache.sshd.server.session.ServerSession

class NoneCredentialsPasswordAuthenticator implements PasswordAuthenticator {

    @Override
    boolean authenticate(String username, String password, ServerSession session) {
        false
    }

}
