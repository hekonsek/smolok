package net.smolok.service.binding.security

import net.smolok.service.binding.ServiceEvent

class MockAutenticationProvider implements AuthenticationProvider {

    private final String username

    private final String tenant

    MockAutenticationProvider(String username, String tenant) {
        this.username = username
        this.tenant = tenant
    }

    @Override
    Credentials authenticate(ServiceEvent serviceEvent) {
        new Credentials(username, tenant)
    }

}
