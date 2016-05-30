package smolok.lib.common

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.Validate

import java.util.zip.ZipInputStream

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

    void download(BinaryCoordinates image) {
        Validate.notNull(image.source(), 'Source URL cannot be null.')
        Validate.notNull(image.fileName(), 'Please indicate the name of the target file.')

        def file = downloadedFile(image.fileName)
        if(!file.exists()) {
            LOG.debug('File {} does not exist - downloading...', file.absolutePath)
            file.parentFile.mkdirs()
            copyLarge(image.source().openStream(), new FileOutputStream(file))
            LOG.debug('Saved downloaded file to {}.', file.absolutePath)

            if(image.extractedFileName != null) {
                def extractedImage = downloadedFile(image.extractedFileName)
                if (!extractedImage.exists()) {
                    def zip = new ZipInputStream(new FileInputStream(file))
                    zip.nextEntry
                    IOUtils.copyLarge(zip, new FileOutputStream(extractedImage))
                    zip.close()
                }
            }
        } else {
            LOG.debug('File {} exists - download skipped.', file)
        }
    }

    // File access operations

    File downloadDirectory() {
        downloadDirectory
    }

    File downloadedFile(String name) {
        new File(downloadDirectory, name)
    }

    static class BinaryCoordinates {

        private final URL source

        private final String fileName

        private final String extractedFileName

        BinaryCoordinates(URL source, String fileName, String extractedFileName) {
            this.source = source
            this.fileName = fileName
            this.extractedFileName = extractedFileName
        }

        BinaryCoordinates(URL source, String fileName) {
            this(source, fileName, null)
        }


        URL source() {
            source
        }

        String fileName() {
            fileName
        }

        String extractedFileName() {
            extractedFileName
        }

    }

}