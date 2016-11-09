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

    @Override
    void configure() throws Exception {
        from("netty4-http:http://localhost:${port}/execute?matchOnUriPrefix=true").process {
            def requestUri = it.getIn().getHeader(HTTP_URI, String).substring(9)
            def command = new String(Base64.decoder.decode(requestUri))
            def commandId = Uuids.uuid()
            commandDispatcher.handleCommand(commandId, command.split(' '))
            it.in.body = commandId
        }

        from("netty4-http:http://localhost:${port}/output?matchOnUriPrefix=true").process {
            def commandId = it.getIn().getHeader(HTTP_URI, String).substring(8)
            def xxx = commandId.split('/')
            def lines = readableOutputSink.output(xxx[0], xxx[1].toInteger())
            if(lines == null) {
                it.in.body = '-1'
            } else {
                it.in.body = "${xxx[1].toInteger() + lines.size()}\n" + lines.join('\n')
            }
        }
    }

}
