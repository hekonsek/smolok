package net.smolok.service.binding.security

import net.smolok.service.binding.ServiceEvent

interface AuthenticationProvider {

    Credentials authenticate(ServiceEvent serviceEvent)

}