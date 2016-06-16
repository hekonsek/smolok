package smolok.lib.common

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Reflections.isPojo
import static smolok.lib.common.Reflections.wrappableAwareInstanceOf

class ReflectionsTest {

    @Test
    void shouldDetectThatIntIsNotString() {
        assertThat(wrappableAwareInstanceOf(int.class, String.class)).isFalse()
    }

    @Test
    void shouldDetectThatIntIsInt() {
        assertThat(wrappableAwareInstanceOf(int.class, int.class)).isTrue()
    }

    @Test
    void shouldDetectThatIntegerIsInteger() {
        assertThat(wrappableAwareInstanceOf(Integer.class, Integer.class)).isTrue()
    }

    @Test
    void shouldDetectThatIntIsInteger() {
        assertThat(wrappableAwareInstanceOf(int.class, Integer.class)).isTrue()
    }

    @Test
    void shouldDetectThatIntegerIsInt() {
        assertThat(wrappableAwareInstanceOf(Integer.class, int.class)).isTrue()
    }

    @Test
    void shouldDetectThatIntIsNotPojo() {
        assertThat(isPojo(10.class)).isFalse()
    }

    @Test
    void shouldDetectThatMapIsNotPojo() {
        assertThat(isPojo(Map.class)).isFalse()
    }

    @Test
    void shouldDetectThatListIsNotPojo() {
        assertThat(isPojo(List.class)).isFalse()
    }

    @Test
    void shouldDetectThatObjectIsNotPojo() {
        assertThat(isPojo(Object.class)).isFalse()
    }

    @Test
    void shouldDetectPojo() {
        assertThat(isPojo(Pojo.class)).isTrue()
    }

    static class Pojo {}

}
