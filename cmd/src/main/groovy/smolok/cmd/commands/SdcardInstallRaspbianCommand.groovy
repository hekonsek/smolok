package smolok.cmd.commands

import smolok.cmd.Command
import smolok.cmd.OutputSink
import smolok.lib.common.DownloadManager
import smolok.lib.process.ProcessManager

import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.DownloadManager.*

class SdcardInstallRaspbianCommand implements Command {

    // Logger

    private static final LOG = getLogger(SdcardInstallRaspbianCommand)

    private final String devicesDirectory

    private final DownloadManager downloadManager

    private final ProcessManager processManager

    private final BinaryCoordinates image

    // Constructor
    SdcardInstallRaspbianCommand(String devicesDirectory, DownloadManager downloadManager, ProcessManager processManager, BinaryCoordinates image) {
        this.devicesDirectory = devicesDirectory
        this.downloadManager = downloadManager
        this.processManager = processManager
        this.image = image
    }

    // Command operations

    @Override
    boolean supports(String... command) {
        command[0] == 'sdcard' && command[1] == 'install-raspbian'
    }

    @Override
    void handle(OutputSink outputSink, String... command) {
        def device = command[2]

        downloadManager.download(image)

        outputSink.out('Writing image to SD card...')
        def extractedImage = downloadManager.downloadedFile(image.extractedFileName())
        processManager.execute("dd", "bs=4M", "if=${extractedImage}", "of=${devicesDirectory}/${device}").forEach {
            outputSink.out(it)
        }
        processManager.execute('sync').forEach {
            outputSink.out(it)
        }
    }

}