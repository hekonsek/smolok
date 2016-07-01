package smolok.lib.ingester

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class IngesterTest {

    def ingester = new Ingester('{"foo.bar": {"baz": "randomString(10)", "constant": 1000, "groovy": "groovy(2+2)"}}')

    @Test
    void shouldInjectRandomString() {
        // When
        def record = ingester.nextRecord()

        // Then
        assertThat(record['foo.bar'].baz).isInstanceOf(String.class)
    }

    @Test
    void shouldInjectRandomStringFromRange() {
        // When
        def record = ingester.nextRecord()

        // Then
        assertThat(record['foo.bar'].baz as int).isBetween(0, 10)
    }

    @Test
    void shouldInjectContstantInt() {
        // When
        def record = ingester.nextRecord()

        // Then
        assertThat(record['foo.bar'].constant).isEqualTo(1000)
    }

    @Test
    void shouldInjectGroovySum() {
        // When
        def record = ingester.nextRecord()

        // Then
        assertThat(record['foo.bar'].groovy).isEqualTo(4)
    }

}
