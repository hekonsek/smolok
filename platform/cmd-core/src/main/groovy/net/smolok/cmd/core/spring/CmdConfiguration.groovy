package net.smolok.cmd.core.spring

import net.smolok.cmd.commands.*
import net.smolok.cmd.core.Command
import net.smolok.cmd.core.CommandDispatcher
import net.smolok.cmd.core.GuavaCacheOutputSink
import net.smolok.cmd.core.OutputSink
import net.smolok.cmd.endpoints.RestEndpoint
import net.smolok.lib.download.DownloadManager
import net.smolok.lib.endpoint.Endpoint
import net.smolok.paas.Paas
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.lib.docker.Docker
import smolok.lib.process.ProcessManager
import smolok.status.StatusResolver

/**
 * Configuration of the command line tool.
 */
@Configuration
class CmdConfiguration {

    @Bean
    @ConditionalOnMissingBean
    OutputSink outputSink() {
        new GuavaCacheOutputSink()
    }

    @Bean
    @ConditionalOnMissingBean
    CommandDispatcher commandDispatcher(OutputSink outputSink, List<Command> commands) {
        new CommandDispatcher(outputSink, commands)
    }

    @Bean
    RestEndpoint restEndpoint(CommandDispatcher commandDispatcher, OutputSink readableOutputSink, @Value('${agent.rest.port:8081}') int port) {
        new RestEndpoint(commandDispatcher, readableOutputSink, port)
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

    @Bean
    AdapterStartCommand adapterStartCommand(Paas paas) {
        new AdapterStartCommand(paas)
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

    // Zeppelin commands

    @Bean
    ZeppelinStartCommand zeppelinStartCommand(Docker docker) {
        new ZeppelinStartCommand(docker)
    }

}
