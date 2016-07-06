package smolok.cmd

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class BaseCommandTest {

    def command = new TestCommand()

    @Test
    void shouldSupportCommand() {
        def supports = command.supports('this', 'is', 'my', 'command')
        assertThat(supports).isTrue()
    }

    @Test
    void shouldNotSupportPartialCommand() {
        def supports = command.supports('this', 'is', 'my')
        assertThat(supports).isFalse()
    }

    @Test
    void shouldParseOption() {
        def fooValue = command.option(['--foo=bar'] as String[], 'foo')
        assertThat(fooValue).isPresent().contains('bar')
    }

    @Test
    void shouldProvideDefaultValueForOption() {
        def fooValue = command.option([''] as String[], 'foo', 'bar')
        assertThat(fooValue).isEqualTo('bar')
    }

    static class TestCommand extends BaseCommand {

        TestCommand() {
            super('this', 'is', 'my', 'command')
        }

        @Override
        void handle(OutputSink outputSink, String... command) {

        }

    }

}