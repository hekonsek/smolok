package smolok.service.binding

import org.apache.camel.Exchange

class MockAutenticationProvider implements AuthenticationProvider {

    private final String username

    private final String tenant

    MockAutenticationProvider(String username, String tenant) {
        this.username = username
        this.tenant = tenant
    }

    @Override
    Credentials authenticate(Exchange exchange) {
        new Credentials(username, tenant)
    }

}
