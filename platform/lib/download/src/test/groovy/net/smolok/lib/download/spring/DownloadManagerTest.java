package net.smolok.lib.download.spring;

import com.google.common.io.Files;
import net.smolok.lib.download.DownloadManager;
import net.smolok.lib.download.DownloadManager.BinaryCoordinates;
import net.smolok.lib.download.FileDownloadException;
import net.smolok.lib.download.UnsupportedCompressionFormatException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import smolok.lib.common.Properties;
import smolok.lib.process.spring.ProcessManagerConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.io.Files.createTempDir;
import static org.assertj.core.api.Assertions.assertThat;
import static smolok.lib.common.Uuids.uuid;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DownloadManagerConfiguration.class, ProcessManagerConfiguration.class})
public class DownloadManagerTest {

    @Autowired
    DownloadManager downloadManager;

    @BeforeClass
    public static void beforeClass() {
        Properties.setSystemStringProperty("download.directory", createTempDir().getAbsolutePath());
    }

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

    @Test
    public void shouldDecompressTarGz() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(
                new File("src/test/compressedDirectory.tar.gz").toURI().toURL(),
                "compressedDirectory.tar.gz", "compressedDirectory"));

        // Then
        long uncompressedDirectory = downloadManager.downloadedFile("compressedDirectory").list().length;
        assertThat(uncompressedDirectory).isGreaterThan(0L);
    }

    @Test(expected = UnsupportedCompressionFormatException.class)
    public void shouldHandleUnsupportedCompressionFormat() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(new File("src/test/invalidCompressionFormat.xyz").toURI().toURL(),
                "invalidCompressionFormat.xyz", "invalidCompressionFormat"));
    }

    @Test(expected = FileDownloadException.class)
    public void shouldHandleInvalidHost() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(new URL("http://smolok-" + uuid() + ".com"), uuid()));
    }

}
