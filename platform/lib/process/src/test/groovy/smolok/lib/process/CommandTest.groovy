package smolok.lib.process

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.process.Command.sudo
import static smolok.lib.process.CommandBuilder.cmd

class CommandTest {

    @Test
    void shouldCreateCommandWithSudoEnabled() {
        def command = sudo('foo')
        assertThat(command.sudo()).isTrue()
    }

    @Test
    void shouldCreateCommandWithSudoDisabled() {
        def command = cmd('foo').build()
        assertThat(command.sudo()).isFalse()
    }

    @Test
    void shouldParseStringBySpace() {
        def command = cmd('foo bar').build()
        assertThat(command.command()).isEqualTo(['foo', 'bar'])
    }

}
