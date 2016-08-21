package smolok.lib.common

import static java.util.UUID.randomUUID;

/**
 * Utilities for working with the UUIDs.
 */
final class Uuids {

    private Uuids() {
    }

    static String uuid() {
        return randomUUID().toString();
    }

}
