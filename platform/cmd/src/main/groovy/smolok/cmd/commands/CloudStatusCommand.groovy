package smolok.cmd.commands

import smolok.cmd.Command
import smolok.cmd.OutputSink
import smolok.status.StatusResolver

class CloudStatusCommand implements Command {

    private final StatusResolver statusResolver

    CloudStatusCommand(StatusResolver statusResolver) {
        this.statusResolver = statusResolver
    }

    // Handler operations

    @Override
    boolean supports(String... command) {
        command[0] == 'cloud' && command[1] == 'status'
    }

    @Override
    void handle(OutputSink outputSink, String... command) {
        statusResolver.status().each {
            outputSink.out("${it.key()}\t${it.value()}\t${it.warning() ? 'Warning!' : ''}")
        }
    }

}