package smolok.service.binding

class Credentials {

    private final String username

    private final String tenant

    Credentials(String username, String tenant) {
        this.username = username
        this.tenant = tenant
    }

    String username() {
        username
    }

    String tenant() {
        tenant
    }

}
