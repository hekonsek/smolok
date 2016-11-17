package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommandHandler
import net.smolok.cmd.spi.OutputSink
import smolok.status.StatusResolver

class CloudStatusCommandHandler extends BaseCommandHandler {

    private final StatusResolver statusResolver

    CloudStatusCommandHandler(StatusResolver statusResolver) {
        super('cloud', 'status')
        this.statusResolver = statusResolver
    }

    // Handler operations

    @Override
    void handle(OutputSink outputSink, String commandId, String... command) {
        statusResolver.status().each {
            outputSink.out(commandId, "${it.key()}\t${it.value()}\t${it.warning() ? 'Warning!' : ''}")
        }
    }

}