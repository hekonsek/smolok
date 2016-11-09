package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommand
import net.smolok.cmd.core.OutputSink
import net.smolok.lib.endpoint.Endpoint

class EndpointCommand extends BaseCommand {

    private final Endpoint endpoint

    EndpointCommand(Endpoint endpoint) {
        super(['endpoint'])
        this.endpoint = endpoint
    }

    @Override
    void handle(OutputSink outputSink, String commandId, String... command) {
        def endpointUri = command[1]
        def body = command [2]

        def response = endpoint.request(endpointUri, body)

        outputSink.out(commandId, response)
    }

}
