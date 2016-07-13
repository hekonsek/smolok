package smolok.lib.process

import org.junit.Test

import java.util.concurrent.ExecutionException

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.fail
import static smolok.lib.process.ExecutorBasedProcessManager.command

class DefaultProcessManagerTest {

    // Collaborators fixtures

    def processManager = new DefaultProcessManager()

    // Tests

    @Test
    void shouldBeAbleToExecuteEcho() {
        def canExecuteEcho = processManager.canExecute('echo')
        assertThat(canExecuteEcho).isTrue()
    }

    @Test
    void shouldNotBeAbleToExecuteRandomCommand() {
        def canExecuteEcho = processManager.canExecute('invalidCommand')
        assertThat(canExecuteEcho).isFalse()
    }

    @Test
    void shouldRunEcho() {
        def output = processManager.execute('echo', 'foo')
        assertThat(output).isEqualTo(['foo'])
    }

    @Test
    void shouldHandleInvalidCommand() {
        try {
            processManager.execute('invalidCommand')
        } catch (ProcessExecutionException e) {
            assertThat(e).hasCauseInstanceOf(IOException.class)
            return
        }
        fail('Expected process exception')
    }

    @Test
    void shouldRunEchoAsynchronously() {
        def output = processManager.executeAsync('echo', 'foo')
        assertThat(output.get()).isEqualTo(['foo'])
    }

    @Test
    void shouldHandleInvalidAsynchronousCommand() {
        try {
            processManager.executeAsync('invalidCommand').get()
        } catch (ExecutionException e) {
            assertThat(e).hasCauseInstanceOf(ProcessExecutionException.class)
            return
        }
        fail('Expected process exception')
    }

    @Test
    void shouldParseCommandWithSpaces() {
        def output = processManager.execute(command('echo foo'))
        assertThat(output).isEqualTo(['foo'])
    }

    @Test
    void shouldParseCommandWithDoubleSpaces() {
        def output = processManager.execute(command('echo  foo'))
        assertThat(output).isEqualTo(['foo'])
    }

    @Test
    void shouldParseCommandWithNewLines() {
        def output = processManager.execute(command('echo\nfoo'))
        assertThat(output).isEqualTo(['foo'])
    }

}
