package smolok.lib.process

import org.assertj.core.api.Assertions
import org.junit.Test

import static smolok.lib.common.Properties.setSystemStringProperty
import static smolok.lib.process.SudoResolver.resolveSudo

class SudoResolverTest {

    @Test
    void nonRootWithNonEmptyPasswordShouldUseSudoInPipe() {
        // Given
        setSystemStringProperty('user.name', 'notRoot')
        def command = CommandBuilder.sudo('echo foo').sudoPassword('nonEmptyPassword').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand.last()).contains('sudo')
    }

    @Test
    void nonRootWithEmptyPasswordShouldUseSudoPrefix() {
        // Given
        setSystemStringProperty('user.name', 'notRoot')
        def command = CommandBuilder.sudo('echo foo').sudoPassword('').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand.first()).isEqualTo('sudo')
    }

    @Test
    void rootShouldNotUseSudo() {
        // Given
        setSystemStringProperty('user.name', 'root')
        def command = CommandBuilder.sudo('echo foo').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand).isEqualTo(['echo', 'foo'])
    }

}
