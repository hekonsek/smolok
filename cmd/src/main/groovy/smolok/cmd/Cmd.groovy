package smolok.cmd

import org.springframework.context.ConfigurableApplicationContext
import smolok.bootstrap.Smolok

class Cmd extends Smolok {

    @Override
    ConfigurableApplicationContext run(String... command) {
        def context = super.run(command)
        context.getBean(CommandDispatcher.class).handleCommand(command)
        context
    }

    public static void main(String... args) {
        new Cmd().run(args)
    }

}
