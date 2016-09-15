package net.smolok.lib.geo

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory

class Geofencing {

    static boolean isPointWithinPolygon(Point point, List<Point> polygon) {
        def polygonCoordinates = polygon.collect{ new Coordinate(it.lat(), it.lng()) }
        if(polygonCoordinates.first() != polygonCoordinates.last()) {
            polygonCoordinates << polygonCoordinates.first()
        }

        def geometryFactory = new GeometryFactory()
        def jtsPolygon = geometryFactory.createPolygon(polygonCoordinates.toArray(new Coordinate[0]))
        geometryFactory.createPoint(new Coordinate(point.lat(), point.lng())).within(jtsPolygon)
    }

}
