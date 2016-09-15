package net.smolok.lib.geo

import org.apache.commons.io.IOUtils
import org.assertj.core.api.Assertions
import org.junit.Test

import java.nio.file.Files

import static org.assertj.core.api.Assertions.assertThat

class GoogleMapsTest {

    @Test
    void shouldDownloadMapAsPng() {
        def url = GoogleMaps.renderRouteUrl(new Point(49.823873, 19.041077), new Point(49.829472, 19.077234))
        def mapFile = File.createTempFile('xxx', 'xxx')
        IOUtils.copy(url.openStream(), new FileOutputStream(mapFile))

        // Then
        def mapContentType = Files.probeContentType(mapFile.toPath())
        assertThat(mapContentType).isEqualTo('image/png')
    }

}
