package smolok.paas

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * Represents a value of the metric.
 */
@CompileStatic
@Immutable
@ToString(includeNames=true, includeFields=true)
class ServiceEndpoint {

    String name

    String host

    int port

}