package smolok.cmd.commands

import net.smolok.cmd.commands.SparkSubmitCommand
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
        assertThat(container.arguments()).isEqualTo(['--master=spark://localhost:7077', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
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

    @Test
    void shouldUseDefaultMasterUrl() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.arguments()).isEqualTo(['--master=spark://localhost:7077', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }

    @Test
    void shouldUseDefaultMasterUrlForClientMode() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit --deploy-mode=client artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.arguments()).isEqualTo(['--master=spark://localhost:7077', '--deploy-mode=client', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }

    @Test
    void shouldUseDefaultMasterUrlForClusterMode() {
        // Given
        given(docker.execute(containerCaptor.capture())).willReturn([])
        def cmd = new SparkSubmitCommand(docker)

        // When
        cmd.handle(null, command('spark submit --deploy-mode=cluster artifact argument1 argument2'))

        // Then
        def container = containerCaptor.value
        assertThat(container.arguments()).isEqualTo(['--master=spark://localhost:6066', '--deploy-mode=cluster', '/var/smolok/spark/jobs/artifact', 'argument1', 'argument2'])
    }
}
