package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommandHandler
import net.smolok.cmd.spi.OutputSink
import net.smolok.paas.Paas

class CloudStartCommandHandler extends BaseCommandHandler {

    // Collaborators

    private final Paas paas

    // Constructors

    CloudStartCommandHandler(Paas paas) {
        super('cloud', 'start')
        this.paas = paas
    }

    // CommandHandler operations

    @Override
    void handle(OutputSink outputSink, String commandId, String... command) {
        outputSink.out(commandId, 'Starting Smolok Cloud...')
        paas.start()
        outputSink.out(commandId, 'Smolok Cloud started.')
    }

}