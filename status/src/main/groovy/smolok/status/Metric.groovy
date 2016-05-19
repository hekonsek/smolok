package smolok.status

import groovy.transform.Immutable

/**
 * Metric describing status of a given subject.
 */
@Immutable
class Metric {

    /**
     * Key uniquely identifying the metric.
     */
    String key

    String value

    boolean warning

}