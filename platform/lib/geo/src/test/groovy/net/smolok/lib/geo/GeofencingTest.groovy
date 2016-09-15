package net.smolok.lib.geo

import org.assertj.core.api.Assertions
import org.junit.Test

import static net.smolok.lib.geo.Geofencing.isPointWithinPolygon
import static org.assertj.core.api.Assertions.assertThat

class GeofencingTest {

    @Test
    void shouldDetectPointWIthinPolygon() {
        def point = new Point(1,1)
        def polygon = [new Point(0,0), new Point(1,2), new Point(2,1)]

        // When
        def isWithin = isPointWithinPolygon(point, polygon)

        // Then
        assertThat(isWithin).isTrue()
    }

    @Test
    void shouldNotDetectPointWIthinPolygon() {
        def point = new Point(100,100)
        def polygon = [new Point(0,0), new Point(1,2), new Point(2,1)]

        // When
        def isWithin = isPointWithinPolygon(point, polygon)

        // Then
        assertThat(isWithin).isFalse()
    }

}
