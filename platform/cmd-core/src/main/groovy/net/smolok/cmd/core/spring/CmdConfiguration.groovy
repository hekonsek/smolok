package net.smolok.cmd.core.spring

import net.smolok.cmd.commands.CloudResetCommand
import net.smolok.cmd.commands.ServiceStartCommand
import net.smolok.lib.download.DownloadManager
import net.smolok.lib.endpoint.Endpoint
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import net.smolok.cmd.core.Command
import net.smolok.cmd.core.CommandDispatcher
import net.smolok.cmd.core.OutputSink
import net.smolok.cmd.core.StdoutOutputSink
import net.smolok.cmd.commands.CloudStartCommand
import net.smolok.cmd.commands.CloudStatusCommand
import net.smolok.cmd.commands.EndpointCommand
import net.smolok.cmd.commands.SdcardInstallRaspbianCommand
import net.smolok.cmd.commands.SparkStartCommand
import net.smolok.cmd.commands.SparkSubmitCommand

import smolok.lib.docker.Docker
import smolok.lib.process.ProcessManager
import smolok.paas.Paas
import smolok.status.StatusResolver

/**
 * Configuration of the command line tool.
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

    // Commands

    @Bean
    CloudStartCommand cloudStartCommand(Paas paas) {
        new CloudStartCommand(paas)
    }

    @Bean
    CloudResetCommand cloudResetCommand(Paas paas) {
        new CloudResetCommand(paas)
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

    @Bean
    EndpointCommand endpointCommand(Endpoint endpoint) {
        new EndpointCommand(endpoint)
    }

    @Bean
    ServiceStartCommand serviceStartCommand(Paas paas) {
        new ServiceStartCommand(paas)
    }

    // Spark commands

    @Bean
    SparkStartCommand sparkStartCommand(Docker docker) {
        new SparkStartCommand(docker)
    }

    @Bean
    SparkSubmitCommand sparkSubmitCommand(Docker docker) {
        new SparkSubmitCommand(docker)
    }

}
