package smolok.cmd.commands

import smolok.cmd.Command
import smolok.cmd.OutputSink

class CloudStartCommand implements Command {

    @Override
    boolean supports(String... command) {
        command[0] == 'cloud' && command[1] == 'start'
    }

    @Override
    void handle(OutputSink outputSink, String... command) {
        outputSink.out('Starting Smolok Cloud...')
        outputSink.out('Smolok Cloud started.')
    }

}