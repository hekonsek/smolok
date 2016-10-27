package smolok.service.binding.security

import smolok.service.binding.ServiceEvent
import smolok.service.binding.security.AuthenticationProvider
import smolok.service.binding.security.Credentials

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
