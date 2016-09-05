package smolok.cmd.commands

import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import smolok.lib.docker.Container
import smolok.lib.docker.Docker

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.BDDMockito.given
import static smolok.lib.process.ExecutorBasedProcessManager.command

class SparkSubmitCommandTest {

    def docker = Mockito.mock(Docker.class)

    def containerCaptor = ArgumentCaptor.forClass(Container.class)

    @Test
    void shouldPassArguments() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.arguments()).isEqualTo(['/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }

    @Test
    void shouldSkipOptionsAndFindJobArtifact() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit --master=foo artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.arguments()).isEqualTo(['--master=foo', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }

    @Test
    void shouldCleanUpContainerByDefault() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit --master=foo artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.cleanUp()).isEqualTo(true)
        assertThat(container.arguments()).isEqualTo(['--master=foo', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }

    @Test
    void shouldHandleKeepLogsOption() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit --master=foo --keep-logs artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.cleanUp()).isEqualTo(false)
        assertThat(container.arguments()).isEqualTo(['--master=foo', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }
}
