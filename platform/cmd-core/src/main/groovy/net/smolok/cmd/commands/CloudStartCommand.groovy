package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommand
import net.smolok.cmd.core.OutputSink
import net.smolok.paas.Paas

class CloudStartCommand extends BaseCommand {

    // Collaborators

    private final Paas paas

    // Constructors

    CloudStartCommand(Paas paas) {
        super('cloud', 'start')
        this.paas = paas
    }

    // Command operations

    @Override
    void handle(OutputSink outputSink, String commandId, String... command) {
        outputSink.out(commandId, 'Starting Smolok Cloud...')
        paas.start()
        outputSink.out(commandId, 'Smolok Cloud started.')
    }

}