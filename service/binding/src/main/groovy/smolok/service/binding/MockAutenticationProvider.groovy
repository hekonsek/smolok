package smolok.service.binding

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
