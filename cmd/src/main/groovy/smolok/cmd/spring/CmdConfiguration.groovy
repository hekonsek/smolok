package smolok.cmd.spring

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.cmd.Command
import smolok.cmd.CommandDispatcher
import smolok.cmd.OutputSink
import smolok.cmd.StdoutOutputSink
import smolok.cmd.commands.CloudStartCommand
import smolok.cmd.commands.CloudStatusCommand
import smolok.paas.Paas
import smolok.status.StatusResolver

/**
 * Configuration of command line tool.
 */
@Configuration
class CmdConfiguration {

    @Bean
    @ConditionalOnMissingBean
    OutputSink outputSink() {
        new StdoutOutputSink()
    }

    @Bean
    @ConditionalOnMissingBean
    CommandDispatcher commandDispatcher(OutputSink outputSink, List<Command> commands) {
        new CommandDispatcher(outputSink, commands)
    }

    // Commands

    @Bean
    CloudStartCommand cloudStartCommand(Paas paas) {
        new CloudStartCommand(paas)
    }

    @Bean
    CloudStatusCommand cloudStatusCommand(StatusResolver statusResolver) {
        new CloudStatusCommand(statusResolver)
    }

}
