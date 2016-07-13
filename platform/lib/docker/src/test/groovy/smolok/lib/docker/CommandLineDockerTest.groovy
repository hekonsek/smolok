package smolok.lib.docker

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class CommandLineDockerTest {

    @Test
    void shouldRunAsDaemon() {
        // Given
        def container = new ContainerBuilder('image').build()

        // When
        def command = CommandLineDocker.buildRunCommand(container, true)

        // Then
        assertThat(command).contains(' -d ')
    }

    @Test
    void shouldNotRunAsDaemon() {
        // Given
        def container = new ContainerBuilder('image').build()

        // When
        def command = CommandLineDocker.buildRunCommand(container, false)

        // Then
        assertThat(command).doesNotContain(' -d ')
    }

    @Test
    void shouldNotMountVolumes() {
        // Given
        def container = new ContainerBuilder('image').build()

        // When
        def command = CommandLineDocker.buildRunCommand(container, false)

        // Then
        assertThat(command).doesNotContain(' -v ')
    }

    @Test
    void shouldMountVolumes() {
        // Given
        def container = new ContainerBuilder('image').volumes([foo: 'bar']).build()

        // When
        def command = CommandLineDocker.buildRunCommand(container, false)

        // Then
        assertThat(command).contains(' -v foo:bar ')
    }

    @Test
    void shouldPassEnvironment() {
        // Given
        def container = new ContainerBuilder('image').environment([foo: 'bar']).build()

        // When
        def command = CommandLineDocker.buildRunCommand(container, false)

        // Then
        assertThat(command).contains(' -e foo=bar ')
    }

}
