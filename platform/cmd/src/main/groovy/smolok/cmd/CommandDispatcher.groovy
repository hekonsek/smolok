package smolok.cmd

import org.apache.commons.lang3.Validate

import static org.slf4j.LoggerFactory.getLogger

/**
 * Dispatcher find appropriate handler to execute the command and perform the actual execution.
 */
class CommandDispatcher {

    private final static LOG = getLogger(CommandDispatcher.class)

    private final OutputSink outputSink

    private final List<Command> commands

    // Constructors

    CommandDispatcher(OutputSink outputSink, List<Command> commands) {
        this.outputSink = outputSink
        this.commands = commands
    }

    // Operations

    void handleCommand(String... command) {
        def flatCommand = command.join(' ')
        LOG.debug('About to execute command {}', flatCommand)
        try {
            def handler = commands.find { it.supports(command) }
            Validate.notNull(handler, "Cannot find handler for the command: ${flatCommand}")
            handler.handle(outputSink, command)
        } catch (Exception e) {
            outputSink.out(e.message)
            LOG.info('Exception catch during command execution:', e)
        }
    }

}
