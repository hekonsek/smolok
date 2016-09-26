package smolok.cmd.commands

import smolok.cmd.BaseCommand
import smolok.cmd.OutputSink
import smolok.status.StatusResolver

class CloudStatusCommand extends BaseCommand {

    private final StatusResolver statusResolver

    CloudStatusCommand(StatusResolver statusResolver) {
        super('cloud', 'status')
        this.statusResolver = statusResolver
    }

    // Handler operations

    @Override
    void handle(OutputSink outputSink, String... command) {
        statusResolver.status().each {
            outputSink.out("${it.key()}\t${it.value()}\t${it.warning() ? 'Warning!' : ''}")
        }
    }

}