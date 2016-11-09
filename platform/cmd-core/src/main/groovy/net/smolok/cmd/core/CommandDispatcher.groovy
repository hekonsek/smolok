package net.smolok.cmd.core

import org.apache.commons.lang3.Validate

import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties

/**
 * Dispatcher find appropriate handler to execute the command and perform the actual execution.
 */
class CommandDispatcher {

    // Logger

    private final static LOG = getLogger(CommandDispatcher.class)

    // Collaborators

    private final OutputSink outputSink

    private final List<Command> commands

    // Constructors

    CommandDispatcher(OutputSink outputSink, List<Command> commands) {
        this.outputSink = outputSink
        this.commands = commands
    }

    // Operations

    void handleCommand(String commandId, String... command) {
        try {
            LOG.debug('Executing command: {}', command.toList())
            if (command.length == 0) {
                outputSink.out(commandId, 'Cannot execute empty command. Use --help option to list available commands.')
                return
            }

            if (command[0] == '--help') {
                def smolokVersion = artifactVersionFromDependenciesProperties('net.smolok', 'smolok-paas').get()
                outputSink.out(commandId, "Welcome to Smolok v${smolokVersion}.")
                return
            }

            def flatCommand = command.join(' ')
            LOG.debug('About to execute command {}', flatCommand)
            try {
                def handler = commands.find { it.supports(command) }
                Validate.notNull(handler, "Cannot find handler for the command: ${flatCommand}")

                if (handler.helpRequested(command)) {
                    outputSink.out(commandId, handler.help())
                } else {
                    handler.handle(outputSink, commandId, command)
                }
            } catch (Exception e) {
                outputSink.out(commandId, e.message)
                LOG.info('Exception catch during command execution:', e)
            }
        }  finally {
            outputSink.markAsDone(commandId)
        }
    }

}
