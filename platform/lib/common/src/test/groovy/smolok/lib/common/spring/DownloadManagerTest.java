package smolok.lib.common.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import smolok.lib.common.DownloadManager;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DownloadManagerConfiguration.class)
public class DownloadManagerTest {

    @Autowired
    DownloadManager downloadManager;

    @Test
    public void shouldDownloadFile() throws MalformedURLException {
        // When
        downloadManager.download(new DownloadManager.BinaryCoordinates(
                new URL("http://search.maven.org/remotecontent?filepath=org/wildfly/swarm/guava/1.0.0.Alpha8/guava-1.0.0.Alpha8.jar"),
                "guava.jar"));

        // Then
        long guavaSize = downloadManager.downloadedFile("guava.jar").length();
        assertThat(guavaSize).isGreaterThan(0L);
    }

}
