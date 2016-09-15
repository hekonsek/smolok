package net.smolok.lib.geo

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Point {

    private final double lat

    private final double lng

    Point(double lat, double lng) {
        this.lat = lat
        this.lng = lng
    }

    double lat() {
        return lat
    }

    double lng() {
        return lng
    }

}
