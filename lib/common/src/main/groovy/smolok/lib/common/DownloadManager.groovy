package smolok.lib.common

import org.apache.commons.lang3.Validate

import static org.apache.commons.io.IOUtils.copyLarge
import static org.slf4j.LoggerFactory.getLogger

/**
 * Downloads and caches binary files.
 */
class DownloadManager {

    // Logger

    private final static LOG = getLogger(DownloadManager.class)

    // Members

    private final File downloadDirectory

    // Constructors

    DownloadManager(File downloadDirectory) {
        this.downloadDirectory = downloadDirectory

        downloadDirectory.mkdirs()
    }

    // Download operations

    void download(URL source, String targetName) {
        Validate.notNull(source, 'Source URL cannot be null.')
        Validate.notNull(targetName, 'Please indicate the name of the target file.')

        def imageZip = downloadedFile(targetName)
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
