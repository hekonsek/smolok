package smolok.cmd.commands

import smolok.cmd.BaseCommand
import smolok.cmd.OutputSink
import smolok.paas.Paas

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
    void handle(OutputSink outputSink, String... command) {
        outputSink.out('Starting Smolok Cloud...')
        paas.start()
        outputSink.out('Smolok Cloud started.')
    }

}