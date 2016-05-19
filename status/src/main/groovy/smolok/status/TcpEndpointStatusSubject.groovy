package smolok.status

import groovy.transform.Immutable

@Immutable
class TcpEndpointStatusSubject {

    String name

    String host

    int port

}
