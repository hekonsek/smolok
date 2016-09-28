package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommand
import net.smolok.cmd.core.OutputSink
import smolok.paas.Paas

class ServiceStartCommand extends BaseCommand {

    // Collaborators

    private final Paas paas

    // Constructors

    ServiceStartCommand(Paas paas) {
        super('service-start')
        this.paas = paas
    }

    // Command operations

    @Override
    void handle(OutputSink outputSink, String... command) {
        def serviceLocator = command[1]
        outputSink.out("Starting service '${serviceLocator}'...")
        paas.startService(serviceLocator)
        outputSink.out('Service started.')
    }

}