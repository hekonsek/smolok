package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommandHandler
import net.smolok.cmd.spi.OutputSink
import net.smolok.paas.Paas

class CloudResetCommandHandler extends BaseCommandHandler {

    // Collaborators

    private final Paas paas

    // Constructors

    CloudResetCommandHandler(Paas paas) {
        super('cloud', 'reset')
        this.paas = paas
    }

    @Override
    Optional<List<String>> supportedOptions() {
        Optional.of([])
    }

    // CommandHandler operations

    @Override
    void handle(OutputSink outputSink, String commandId, String... command) {
        validateOptions(command)

        outputSink.out(commandId, 'Resetting Smolok Cloud...')
        paas.reset()
        outputSink.out(commandId, 'Smolok Cloud has been reset.')
    }

}