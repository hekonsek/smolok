package net.smolok.cmd.core

import net.smolok.cmd.spi.CommandHandler
import net.smolok.cmd.spi.OutputSink
import org.apache.commons.lang3.Validate

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties
import static smolok.lib.common.Uuids.uuid

/**
 * Dispatcher find appropriate handler to execute the command and perform the actual execution.
 */
class CommandDispatcher {

    // Logger

    private final static LOG = getLogger(CommandDispatcher.class)

    // Collaborators

    private final OutputSink outputSink

    private final List<CommandHandler> commands

    private final ExecutorService executor = Executors.newCachedThreadPool()

    // Constructors

    CommandDispatcher(OutputSink outputSink, List<CommandHandler> commands) {
        this.outputSink = outputSink
        this.commands = commands
    }

    // Operations

    String handleCommand(String... command) {
        def commandId = uuid()
        executor.submit(new Runnable() {
            @Override
            void run() {
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
        })
        commandId
    }

}
