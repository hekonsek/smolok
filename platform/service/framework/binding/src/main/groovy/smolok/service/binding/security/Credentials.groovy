package smolok.service.binding.security

import groovy.transform.CompileStatic

/**
 * Represents information about a successfully authenticated user.
 */
@CompileStatic
class Credentials {

    private final String username

    private final String tenant

    Credentials(String username, String tenant) {
        this.username = username
        this.tenant = tenant
    }

    // Getters

    String username() {
        username
    }

    String tenant() {
        tenant
    }

}
