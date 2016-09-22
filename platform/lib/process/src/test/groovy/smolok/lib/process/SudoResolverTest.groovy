package smolok.lib.process

import org.assertj.core.api.Assertions
import org.junit.Test

import static smolok.lib.common.Properties.setThreadStringProperty
import static smolok.lib.process.SudoResolver.resolveSudo

class SudoResolverTest {

    @Test
    void rootUseSudo() {
        // Given
        def command = CommandBuilder.sudo('echo foo').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand.last()).contains('sudo')
    }

    @Test
    void rootShouldNotUseSudo() {
        // Given
        setThreadStringProperty('user.name', 'root')
        def command = CommandBuilder.sudo('echo foo').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand).isEqualTo(['echo', 'foo'])
    }

}
