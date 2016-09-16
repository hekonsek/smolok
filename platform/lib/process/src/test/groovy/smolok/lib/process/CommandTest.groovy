package smolok.lib.process

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.process.Command.cmd
import static smolok.lib.process.Command.sudo

class CommandTest {

    @Test
    void shouldCreateCommandWithSudoEnabled() {
        def command = sudo('foo')
        assertThat(command.sudo()).isTrue()
    }

    @Test
    void shouldCreateCommandWithSudoDisabled() {
        def command = cmd('foo')
        assertThat(command.sudo()).isFalse()
    }

}
