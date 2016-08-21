package smolok.lib.common

import groovy.transform.CompileStatic
import org.slf4j.Logger

import static java.util.UUID.randomUUID
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Utilities for working with the UUIDs.
 */
@CompileStatic
final class Uuids {

    // Logger

    private final static Logger LOG = getLogger(Uuids.class)

    // Constructors

    private Uuids() {
    }

    // Operations

    static String uuid() {
        def result = randomUUID().toString()
        LOG.debug('Generated UUID: {}', result)
        result
    }

}
