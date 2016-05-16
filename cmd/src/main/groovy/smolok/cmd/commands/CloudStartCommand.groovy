package smolok.cmd.commands

import smolok.cmd.Command
import smolok.cmd.OutputSink
import smolok.paas.Paas

class CloudStartCommand implements Command {

    // Collaborators

    private final Paas paas

    // Constructors

    CloudStartCommand(Paas paas) {
        this.paas = paas
    }

    // Command operations

    @Override
    boolean supports(String... command) {
        command[0] == 'cloud' && command[1] == 'start'
    }

    @Override
    void handle(OutputSink outputSink, String... command) {
        outputSink.out('Starting Smolok Cloud...')
        paas.start()
        outputSink.out('Smolok Cloud started.')
    }

}