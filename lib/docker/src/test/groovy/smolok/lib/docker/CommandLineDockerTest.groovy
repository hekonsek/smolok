package smolok.lib.docker

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class CommandLineDockerTest {

    @Test
    void shouldRunAsDaemon() {
        // Given
        def container = Container.container('image')

        // When
        def command = CommandLineDocker.buildRunCommand(container, true)

        // Then
        assertThat(command).contains(' -d ')
    }

    @Test
    void shouldNotRunAsDaemon() {
        // Given
        def container = Container.container('image')

        // When
        def command = CommandLineDocker.buildRunCommand(container, false)

        // Then
        assertThat(command).doesNotContain(' -d ')
    }

    @Test
    void shouldNotMountVolumes() {
        // Given
        def container = Container.container('image')

        // When
        def command = CommandLineDocker.buildRunCommand(container, false)

        // Then
        assertThat(command).doesNotContain(' -v ')
    }

    @Test
    void shouldMountVolumes() {
        // Given
        def container = new Container('image', null, null, [foo: 'bar'])

        // When
        def command = CommandLineDocker.buildRunCommand(container, false)

        // Then
        assertThat(command).contains(' -v foo:bar ')
    }

}
