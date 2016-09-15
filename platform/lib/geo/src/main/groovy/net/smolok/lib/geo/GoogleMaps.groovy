package net.smolok.lib.geo

import com.google.common.collect.Lists
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.LatLng

class GoogleMaps {

    private static final String MAP_API_BASE = "https://maps.googleapis.com/maps/api/staticmap";

    public static String encodeRoute(List<Point> coordinates) {
        return PolylineEncoding.encode(coordinates.collect{ new LatLng(it.lat(), it.lng()) })
    }

    public static String encodeRoute(Point... coordinates) {
        return encodeRoute(Arrays.asList(coordinates))
    }

    public static String extractEncodedPath(URL url) {
        String urlString = url.toString();
        return urlString.substring(urlString.indexOf("enc:") + 4);
    }

    public static URL renderRouteUrl(Point... coordinates) {
        return renderRouteUrl(Lists.newArrayList(coordinates));
    }

    public static URL renderRouteUrl(List<Point> coordinates) {
        try {
            return new URL(MAP_API_BASE + "?size=400x400&path=weight:5%7Ccolor:0x0000ff%7Cenc:" + encodeRoute(coordinates));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
