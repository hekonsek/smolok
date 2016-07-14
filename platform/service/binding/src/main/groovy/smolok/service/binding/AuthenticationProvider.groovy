package smolok.service.binding

interface AuthenticationProvider {

    Credentials authenticate(ServiceEvent serviceEvent)

}