package smolok.paas

import groovy.transform.Immutable

@Immutable
class ServiceEndpoint {

    String name

    String host

    int port

}
