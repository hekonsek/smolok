package smolok.cmd.spring

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.cmd.Command
import smolok.cmd.CommandDispatcher
import smolok.cmd.OutputSink
import smolok.cmd.StdoutOutputSink
import smolok.cmd.commands.CloudStartCommand
import smolok.paas.Paas

@Configuration
class CmdConfiguration {

    @Bean
    @ConditionalOnMissingBean
    OutputSink outputSink() {
        new StdoutOutputSink()
    }

    @Bean
    CommandDispatcher commandDispatcher(OutputSink outputSink, List<Command> commands) {
        new CommandDispatcher(outputSink, commands)
    }

    // Commands

    @Bean
    CloudStartCommand cloudStartCommand(Paas paas) {
        new CloudStartCommand(paas)
    }

}
