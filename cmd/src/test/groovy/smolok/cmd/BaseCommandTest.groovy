package smolok.cmd

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class BaseCommandTest {

    def command = new BaseCommand() {
        @Override
        boolean supports(String... command) {
            return false
        }

        @Override
        void handle(OutputSink outputSink, String... command) {
        }
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

}
