package smolok.cmd.commands

import net.smolok.cmd.core.BaseCommand
import net.smolok.cmd.core.OutputSink
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