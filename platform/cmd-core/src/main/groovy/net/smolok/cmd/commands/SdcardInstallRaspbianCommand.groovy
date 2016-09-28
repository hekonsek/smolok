package net.smolok.cmd.commands

import net.smolok.lib.download.DownloadManager
import org.apache.commons.lang3.Validate
import net.smolok.cmd.core.BaseCommand
import net.smolok.cmd.core.OutputSink

import smolok.lib.process.ProcessManager

import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.process.Command.cmd

class SdcardInstallRaspbianCommand extends BaseCommand {

    // Logger

    private static final LOG = getLogger(SdcardInstallRaspbianCommand)

    // Collaborators

    private final DownloadManager downloadManager

    private final ProcessManager processManager

    // Configuration

    private final String devicesDirectory

    private final DownloadManager.BinaryCoordinates image

    // Constructors

    SdcardInstallRaspbianCommand(DownloadManager downloadManager, ProcessManager processManager, String devicesDirectory, DownloadManager.BinaryCoordinates image) {
        super('sdcard', 'install-raspbian')
        this.devicesDirectory = devicesDirectory
        this.downloadManager = downloadManager
        this.processManager = processManager
        this.image = image
    }

    // Command operations

    @Override
    void handle(OutputSink outputSink, String... command) {
        // Device validation
        Validate.isTrue(command.length >= 3, 'Device not specified. Expected device name, for example:\n\n\tsmolok sdcard install-raspbian mmcblk0')
        def device = "${devicesDirectory}/${command[2]}"
        Validate.isTrue(new File(device).exists(), 'Device %s does not exist.', device)

        downloadManager.download(image)

        outputSink.out('Writing image to SD card...')
        def extractedImage = downloadManager.downloadedFile(image.extractedFileName())
        processManager.execute(cmd("dd", "bs=4M", "if=${extractedImage}", "of=${device}")).forEach {
            outputSink.out(it)
        }
        processManager.execute(cmd('sync')).forEach {
            outputSink.out(it)
        }
    }

}