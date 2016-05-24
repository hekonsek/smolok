package smolok.eventbus.client

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static smolok.eventbus.client.Header.arguments

class HeaderTest {

    @Test
    void shouldGenerateHeaderNameForArgument() {
        def argument = arguments('foo').first()
        assertThat(argument.key()).isEqualTo('SMOLOK_ARG0')
    }

}
