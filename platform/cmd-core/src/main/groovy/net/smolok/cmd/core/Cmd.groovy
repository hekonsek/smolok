package net.smolok.cmd.core

import org.eclipse.kapua.locator.spring.KapuaApplication
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext

/**
 * Main class for Smolok command line.
 */
class Cmd extends KapuaApplication {

    def log = LoggerFactory.getLogger(Cmd)

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
