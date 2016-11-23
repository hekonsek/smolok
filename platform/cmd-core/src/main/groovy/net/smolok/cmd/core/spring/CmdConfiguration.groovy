package net.smolok.cmd.core.spring

import net.smolok.cmd.commands.*
import net.smolok.cmd.spi.CommandHandler
import net.smolok.cmd.core.CommandDispatcher
import net.smolok.cmd.core.GuavaCacheOutputSink
import net.smolok.cmd.spi.OutputSink
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
    CommandDispatcher commandDispatcher(OutputSink outputSink, List<CommandHandler> commands) {
        new CommandDispatcher(outputSink, commands)
    }

    @Bean
    RestEndpoint restEndpoint(CommandDispatcher commandDispatcher, OutputSink readableOutputSink, @Value('${agent.rest.port:8081}') int port) {
        new RestEndpoint(commandDispatcher, readableOutputSink, port)
    }

    // Commands

    @Bean
    CloudStartCommandHandler cloudStartCommand(Paas paas) {
        new CloudStartCommandHandler(paas)
    }

    @Bean
    CloudResetCommandHandler cloudResetCommand(Paas paas) {
        new CloudResetCommandHandler(paas)
    }

    @Bean
    CloudStatusCommandHandler cloudStatusCommand(StatusResolver statusResolver) {
        new CloudStatusCommandHandler(statusResolver)
    }

    @Bean
    SdcardInstallRaspbianCommandHandler sdcardInstallRaspbianCommand(
            @Value('${devices.directory:/host/dev}') String devicesDirectory, DownloadManager downloadManager, ProcessManager processManager,
            @Value('${raspbian.image.uri:http://vx2-downloads.raspberrypi.org/raspbian/images/raspbian-2016-02-29/2016-02-26-raspbian-jessie.zip}') URL imageUrl,
            @Value('${raspbian.image.file.name.compressed:2016-02-26-raspbian-jessie.zip}') String compressedFileName,
            @Value('${raspbian.image.file.name.extracted:2016-02-26-raspbian-jessie.img}') String extractedFileName) {
        new SdcardInstallRaspbianCommandHandler(downloadManager, processManager, devicesDirectory, new DownloadManager.BinaryCoordinates(imageUrl, compressedFileName, extractedFileName))
    }

    @Bean
    EndpointCommandHandler endpointCommand(Endpoint endpoint) {
        new EndpointCommandHandler(endpoint)
    }

    @Bean
    ServiceStartCommandHandler serviceStartCommand(Paas paas) {
        new ServiceStartCommandHandler(paas)
    }

    @Bean
    AdapterStartCommandHandler adapterStartCommand(Paas paas) {
        new AdapterStartCommandHandler(paas)
    }

    @Bean
    ServiceExposeCommandHandler exposeCommandHandler(Paas paas, ProcessManager processManager, Docker docker) {
        new ServiceExposeCommandHandler(paas, processManager, docker)
    }


    // Spark commands

    @Bean
    SparkStartCommandHandler sparkStartCommand(Docker docker) {
        new SparkStartCommandHandler(docker)
    }

    @Bean
    SparkSubmitCommandHandler sparkSubmitCommand(Docker docker) {
        new SparkSubmitCommandHandler(docker)
    }

    // Zeppelin commands

    @Bean
    ZeppelinStartCommandHandler zeppelinStartCommand(Docker docker) {
        new ZeppelinStartCommandHandler(docker)
    }

}
