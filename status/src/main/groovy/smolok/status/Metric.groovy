package smolok.status

import groovy.transform.CompileStatic

/**
 * Metric describing status of a given subject.
 */
@CompileStatic
class Metric {

    /**
     * Key uniquely identifying the metric.
     */
    private final String key

    private final Object value

    private final boolean warning

    Metric(String key, Object value, boolean warning) {
        this.key = key
        this.value = value
        this.warning = warning
    }

    static Metric metric(String key, Object value) {
        new Metric(key, value, false)
    }

    String key() {
        key
    }

    Object value() {
        value
    }

    boolean warning() {
        warning
    }

}