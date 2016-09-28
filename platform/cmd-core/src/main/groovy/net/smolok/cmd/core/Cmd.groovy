package net.smolok.cmd.core

import org.springframework.context.ConfigurableApplicationContext
import smolok.bootstrap.Smolok

/**
 * Main class for Smolok command line.
 */
class Cmd extends Smolok {

    @Override
    ConfigurableApplicationContext run(String... command) {
        def context = super.run(command)
        log.debug('About to run command: {}', command.toList())
        context.getBean(CommandDispatcher.class).handleCommand(command)
        context
    }

    public static void main(String... args) {
        new Cmd().run(args).close()
    }

}
