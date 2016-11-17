package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommandHandler
import net.smolok.cmd.spi.OutputSink
import net.smolok.paas.Paas

class ServiceStartCommandHandler extends BaseCommandHandler {

    // Collaborators

    private final Paas paas

    // Constructors

    ServiceStartCommandHandler(Paas paas) {
        super('service-start')
        this.paas = paas
    }

    // CommandHandler operations

    @Override
    void handle(OutputSink outputSink, String commandId, String... command) {
        def serviceLocator = command[1]
        outputSink.out(commandId, "Starting service '${serviceLocator}'...")
        paas.startService(serviceLocator)
        outputSink.out(commandId, 'Service started.')
    }

}