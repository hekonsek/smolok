package net.smolok.lib.download.spring;

import net.smolok.lib.download.DownloadManager;
import net.smolok.lib.download.DownloadManager.BinaryCoordinates;
import net.smolok.lib.download.DownloadProblemException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static smolok.lib.common.Uuids.uuid;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DownloadManagerConfiguration.class)
public class DownloadManagerTest {

    @Autowired
    DownloadManager downloadManager;

    // Tests

    @Test
    public void shouldDownloadFile() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(
                new URL("http://search.maven.org/remotecontent?filepath=org/wildfly/swarm/guava/1.0.0.Alpha8/guava-1.0.0.Alpha8.jar"),
                "guava.jar"));

        // Then
        long guavaSize = downloadManager.downloadedFile("guava.jar").length();
        assertThat(guavaSize).isGreaterThan(0L);
    }

    @Test(expected = DownloadProblemException.class)
    public void shouldHandleInvalidHost() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(new URL("http://smolok-" + uuid() + ".com"), uuid()));
    }

}
