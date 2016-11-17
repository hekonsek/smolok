package net.smolok.cmd.commands

import net.smolok.cmd.core.BaseCommandHandler
import net.smolok.cmd.spi.OutputSink
import net.smolok.lib.endpoint.Endpoint

class EndpointCommandHandler extends BaseCommandHandler {

    private final Endpoint endpoint

    EndpointCommandHandler(Endpoint endpoint) {
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
