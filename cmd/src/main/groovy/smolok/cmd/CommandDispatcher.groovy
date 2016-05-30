package smolok.cmd

import static org.slf4j.LoggerFactory.getLogger

class CommandDispatcher {

    private final static LOG = getLogger(CommandDispatcher.class)

    private final OutputSink outputSink

    private final List<Command> commands

    CommandDispatcher(OutputSink outputSink, List<Command> commands) {
        this.outputSink = outputSink
        this.commands = commands
    }

    void handleCommand(String... command) {
        try {
            commands.find { it.supports(command) }.handle(outputSink, command)
        } catch (Exception e) {
            outputSink.out(e.message)
            LOG.info('Exception catch during command execution:', e)
        }
    }

}
