package net.smolok.cmd.endpoints

import net.smolok.cmd.core.CommandDispatcher
import net.smolok.cmd.core.OutputSink
import org.apache.camel.builder.RouteBuilder
import smolok.lib.common.Uuids

import static org.apache.camel.Exchange.HTTP_URI

class RestEndpoint extends RouteBuilder {

    private final CommandDispatcher commandDispatcher

    private final OutputSink readableOutputSink

    private final int port

    RestEndpoint(CommandDispatcher commandDispatcher, OutputSink readableOutputSink, int port) {
        this.commandDispatcher = commandDispatcher
        this.readableOutputSink = readableOutputSink
        this.port = port
    }

    // Routes

    @Override
    void configure() {
        from("netty4-http:http://localhost:${port}/execute?matchOnUriPrefix=true").process {
            def requestUri = it.getIn().getHeader(HTTP_URI, String).substring(9)
            def command = new String(Base64.decoder.decode(requestUri))
            it.in.body = commandDispatcher.handleCommand(command.split(' '))
        }

        from("netty4-http:http://localhost:${port}/output/{commandId}/{offset}").process {
            def commandId = it.in.getHeader('commandId', String)
            def offset = it.in.getHeader('offset', int)
            def lines = readableOutputSink.output(commandId, offset)
            if(lines == null) {
                it.in.body = '-1'
            } else {
                it.in.body = "${offset + lines.size()}\n" + lines.join('\n')
            }
        }
    }

}
