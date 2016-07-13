package smolok.lib.common

import org.junit.Test

import static java.nio.file.Files.createTempDirectory
import static org.assertj.core.api.Assertions.assertThat

class DownloadManagerTest {

    def downloadManager = new DownloadManager(createTempDirectory('smolok').toFile())

    @Test
    void shouldDownloadFile() {
        // When
        downloadManager.download(new DownloadManager.BinaryCoordinates(
                new URL('http://search.maven.org/remotecontent?filepath=org/wildfly/swarm/guava/1.0.0.Alpha8/guava-1.0.0.Alpha8.jar'),
                'guava.jar'))

        // Then
        def guavaSize = downloadManager.downloadedFile('guava.jar').length()
        assertThat(guavaSize).isGreaterThan(0L)
    }

}
