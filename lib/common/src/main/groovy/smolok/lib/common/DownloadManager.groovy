package smolok.lib.common

import org.slf4j.Logger

import static org.apache.commons.io.IOUtils.copyLarge
import static org.slf4j.LoggerFactory.getLogger

class DownloadManager {

    private final static Logger LOG = getLogger(DownloadManager.class)

    private final File downloadDirectory

    DownloadManager(File downloadDirectory) {
        this.downloadDirectory = downloadDirectory
    }

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

    File downloadedFile(String name) {
        new File(downloadDirectory, name)
    }

    File downloadDirectory() {
        downloadDirectory
    }

}
