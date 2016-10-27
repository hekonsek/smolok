package smolok.service.binding.security

final class CredentialsHolder {

    private static ThreadLocal<Credentials> credentials = new ThreadLocal<>()

    private CredentialsHolder() {
    }

    static void bindCredentials(Credentials credentials) {
        CredentialsHolder.credentials.set(credentials)
    }

    static Credentials credentials() {
        credentials.get()
    }

}
