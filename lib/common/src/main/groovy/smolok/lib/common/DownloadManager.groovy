package smolok.lib.common

import org.slf4j.Logger

import static org.apache.commons.io.IOUtils.copyLarge
import static org.slf4j.LoggerFactory.getLogger

class DownloadManager {

    private final static Logger LOG = getLogger(DownloadManager.class)

    // Members

    private final File downloadDirectory

    // Constructors

    DownloadManager(File downloadDirectory) {
        this.downloadDirectory = downloadDirectory
    }

    // Download operations

    void download(URL source, String name) {
        def imageZip = downloadedFile(name)
        if(!imageZip.exists()) {
            LOG.debug('File {} does not exist - downloading...', imageZip.absolutePath)
            imageZip.parentFile.mkdirs()
            copyLarge(source.openStream(), new FileOutputStream(imageZip))
            LOG.debug('Saved downloaded file to {}.', imageZip.absolutePath)
        } else {
            LOG.debug('File {} exists - download skipped.', imageZip)
        }
    }

    // File access operations

    File downloadDirectory() {
        downloadDirectory
    }

    File downloadedFile(String name) {
        new File(downloadDirectory, name)
    }

}
