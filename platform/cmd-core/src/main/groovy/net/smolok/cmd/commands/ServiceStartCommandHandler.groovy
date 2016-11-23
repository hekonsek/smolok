package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommandHandler
import net.smolok.cmd.spi.OutputSink
import net.smolok.paas.Paas
import net.smolok.service.configuration.api.ConfigurationService

class ServiceStartCommandHandler extends BaseCommandHandler {

    // Collaborators

    private final Paas paas

    private final ConfigurationService configurationService

    // Constructors

    ServiceStartCommandHandler(Paas paas, ConfigurationService configurationService) {
        super('service-start')
        this.paas = paas
        this.configurationService = configurationService
    }

    // CommandHandler operations

    @Override
    void handle(OutputSink outputSink, String commandId, String... command) {
        def serviceLocator = command[1]
        outputSink.out(commandId, "Starting service '${serviceLocator}'...")
        paas.startService(serviceLocator)
        configurationService.put("service.directory.${serviceLocator}", 'true')
        outputSink.out(commandId, 'Service started.')
    }

}