package net.smolok.cmd.endpoints

import net.smolok.cmd.core.CommandDispatcher
import net.smolok.cmd.core.OutputSink
import org.apache.camel.builder.RouteBuilder

import static java.util.Base64.decoder

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
        from("netty4-http:http://localhost:${port}/execute/{command}").transform {
            def command = it.in.getHeader('command', String)
            def decodedCommand = new String(decoder.decode(command))
            commandDispatcher.handleCommand(decodedCommand.split(' '))
        }

        from("netty4-http:http://localhost:${port}/output/{commandId}/{offset}").transform {
            def commandId = it.in.getHeader('commandId', String)
            def offset = it.in.getHeader('offset', int)
            def lines = readableOutputSink.output(commandId, offset)
            if(lines == null) {
                '-1'
            } else {
                def outputBatch = new LinkedList(lines)
                outputBatch.add(0, "${offset + lines.size()}")
                outputBatch.join('___')
            }
        }
    }

}
