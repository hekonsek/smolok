package smolok.service.binding

import org.apache.camel.Exchange

interface AuthenticationProvider {

    Credentials authenticate(Exchange exchange)

}