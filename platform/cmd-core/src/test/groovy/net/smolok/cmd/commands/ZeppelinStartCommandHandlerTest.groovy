package net.smolok.cmd.commands

import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import smolok.lib.docker.Container
import smolok.lib.docker.Docker

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.BDDMockito.given
import static smolok.lib.common.Uuids.uuid
import static smolok.lib.process.ExecutorBasedProcessManager.command

class ZeppelinStartCommandHandlerTest {

    def docker = Mockito.mock(Docker.class)

    def containerCaptor = ArgumentCaptor.forClass(Container.class)

    def commandId = uuid()

    @Test
    void shouldUseDefaultValues() {
        // Given
        given(docker.startService(containerCaptor.capture())).willReturn(null)
        def cmd = new ZeppelinStartCommandHandler(docker)

        // When
        cmd.handle(null, commandId, command('zeppelin start'))

        // Then
        def container = containerCaptor.value
        assertThat(container.environment()).isEqualTo(['MASTER' : 'spark://localhost:7077', 'SPARK_HOME' : '/opt/spark', 'ZEPPELIN_PORT' : '8080',
            'ZEPPELIN_CONF_DIR': '/opt/zeppelin/conf', 'ZEPPELIN_NOTEBOOK_DIR': '/opt/zeppelin/notebook'])
    }

    @Test
    void shouldOverrideMaster() {
        // Given
        given(docker.startService(containerCaptor.capture())).willReturn(null)
        def cmd = new ZeppelinStartCommandHandler(docker)

        // When
        cmd.handle(null, commandId, command('zeppelin start --master=foo'))

        // Then
        def container = containerCaptor.value
        assertThat(container.environment()).containsEntry('MASTER', 'foo')
    }

    @Test
    void shouldSetMultipleSparkOptions() {
        // Given
        given(docker.startService(containerCaptor.capture())).willReturn(null)
        def cmd = new ZeppelinStartCommandHandler(docker)

        // When
        cmd.handle(null, commandId, command("zeppelin start --localIP=bar --deploy-mode cluster --executor-memory 2G"))

        // Then
        def container = containerCaptor.value
        assertThat(container.environment()).containsEntry('SPARK_SUBMIT_OPTIONS', '"--deploy-mode cluster --executor-memory 2G"')
        assertThat(container.environment()).containsEntry('SPARK_LOCAL_IP', 'bar')
    }

    @Test
    void shouldUseCustomNotebookDirectory() {
        // Given
        given(docker.startService(containerCaptor.capture())).willReturn(null)
        def cmd = new ZeppelinStartCommandHandler(docker)

        // When
        cmd.handle(null, commandId, command("zeppelin start --notebookDir=/foo/bar"))

        // Then
        def container = containerCaptor.value
        assertThat(container.environment()).containsEntry('ZEPPELIN_NOTEBOOK_DIR', '/foo/bar')
    }
}
