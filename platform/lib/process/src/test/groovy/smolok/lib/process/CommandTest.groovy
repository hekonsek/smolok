package smolok.lib.process

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Uuids.uuid
import static smolok.lib.process.CommandBuilder.cmd
import static smolok.lib.process.CommandBuilder.sudo

class CommandTest {

    String command = uuid()

    // Tests

    @Test
    void shouldParseStringBySpace() {
        def command = cmd('foo bar').build()
        assertThat(command.command()).isEqualTo(['foo', 'bar'])
    }

    @Test
    void shouldCreateCommandWithSudoEnabled() {
        def command = sudo(command).build()
        assertThat(command.sudo()).isTrue()
    }

    @Test
    void shouldParseCommandWithSudoEnabled() {
        def command = sudo('foo bar').build()
        assertThat(command.sudo()).isTrue()
        assertThat(command.command()).isEqualTo(['foo', 'bar'])
    }

    @Test
    void shouldCreateCommandWithSudoDisabled() {
        def command = cmd(command).build()
        assertThat(command.sudo()).isFalse()
    }

}
