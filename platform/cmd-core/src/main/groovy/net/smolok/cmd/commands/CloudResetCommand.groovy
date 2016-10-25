package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommand
import net.smolok.cmd.core.OutputSink
import net.smolok.paas.Paas

class CloudResetCommand extends BaseCommand {

    // Collaborators

    private final Paas paas

    // Constructors

    CloudResetCommand(Paas paas) {
        super('cloud', 'reset')
        this.paas = paas
    }

    @Override
    Optional<List<String>> supportedOptions() {
        Optional.of([])
    }

    // Command operations

    @Override
    void handle(OutputSink outputSink, String... command) {
        validateOptions(command)

        outputSink.out('Resetting Smolok Cloud...')
        paas.reset()
        outputSink.out('Smolok Cloud has been reset.')
    }

}