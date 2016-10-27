package smolok.service.binding.security

import smolok.service.binding.ServiceEvent
import smolok.service.binding.security.Credentials

interface AuthenticationProvider {

    Credentials authenticate(ServiceEvent serviceEvent)

}