package smolok.cmd.spring

import org.apache.commons.lang3.SystemUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.cmd.Command
import smolok.cmd.CommandDispatcher
import smolok.cmd.OutputSink
import smolok.cmd.StdoutOutputSink
import smolok.cmd.commands.CloudStartCommand
import smolok.cmd.commands.CloudStatusCommand
import smolok.cmd.commands.SdcardInstallRaspbianCommand
import smolok.lib.common.DownloadManager
import smolok.lib.process.ProcessManager
import smolok.paas.Paas
import smolok.status.StatusResolver

/**
 * Configuration of command line tool.
 */
@Configuration
class CmdConfiguration {

    @Bean
    @ConditionalOnMissingBean
    OutputSink outputSink() {
        new StdoutOutputSink()
    }

    @Bean
    @ConditionalOnMissingBean
    CommandDispatcher commandDispatcher(OutputSink outputSink, List<Command> commands) {
        new CommandDispatcher(outputSink, commands)
    }

    @Bean
    DownloadManager downloadManager() {
        new DownloadManager(new File(new File(SystemUtils.userHome, '.smolok'), 'downloads'))
    }

    // Commands

    @Bean
    CloudStartCommand cloudStartCommand(Paas paas) {
        new CloudStartCommand(paas)
    }

    @Bean
    CloudStatusCommand cloudStatusCommand(StatusResolver statusResolver) {
        new CloudStatusCommand(statusResolver)
    }

    @Bean
    SdcardInstallRaspbianCommand sdcardInstallRaspbianCommand(
            @Value('${devices.directory:/host/dev}') String devicesDirectory, DownloadManager downloadManager, ProcessManager processManager,
            @Value('${raspbian.image.uri:http://vx2-downloads.raspberrypi.org/raspbian/images/raspbian-2016-02-29/2016-02-26-raspbian-jessie.zip}') URL imageUrl,
            @Value('${raspbian.image.file.name.compressed:2016-02-26-raspbian-jessie.zip}') String compressedFileName,
            @Value('${raspbian.image.file.name.extracted:2016-02-26-raspbian-jessie.img}') String extractedFileName) {
        new SdcardInstallRaspbianCommand(downloadManager, processManager, devicesDirectory, new DownloadManager.BinaryCoordinates(imageUrl, compressedFileName, extractedFileName))
    }

}
