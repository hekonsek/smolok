package smolok.cmd.commands

import org.apache.commons.lang3.Validate
import smolok.cmd.Command
import smolok.cmd.OutputSink
import smolok.lib.common.DownloadManager
import smolok.lib.process.ProcessManager

import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.DownloadManager.*

class SdcardInstallRaspbianCommand implements Command {

    // Logger

    private static final LOG = getLogger(SdcardInstallRaspbianCommand)

    // Collaborators

    private final DownloadManager downloadManager

    private final ProcessManager processManager

    // Configuration

    private final String devicesDirectory

    private final BinaryCoordinates image

    // Constructors

    SdcardInstallRaspbianCommand(DownloadManager downloadManager, ProcessManager processManager, String devicesDirectory, BinaryCoordinates image) {
        this.devicesDirectory = devicesDirectory
        this.downloadManager = downloadManager
        this.processManager = processManager
        this.image = image
    }

    // Command operations

    @Override
    boolean supports(String... command) {
        command.length >= 2 && command[0] == 'sdcard' && command[1] == 'install-raspbian'
    }

    @Override
    void handle(OutputSink outputSink, String... command) {
        // Device validation
        Validate.isTrue(command.length >= 3, 'Device not specified. Expected device name, for example:\n\n\tsmolok sdcard install-raspbian mmcblk0')
        def device = "${devicesDirectory}/${command[2]}"
        Validate.isTrue(new File(device).exists(), 'Device %s does not exist.', device)

        downloadManager.download(image)

        outputSink.out('Writing image to SD card...')
        def extractedImage = downloadManager.downloadedFile(image.extractedFileName())
        processManager.execute("dd", "bs=4M", "if=${extractedImage}", "of=${device}").forEach {
            outputSink.out(it)
        }
        processManager.execute('sync').forEach {
            outputSink.out(it)
        }
    }

}