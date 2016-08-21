package smolok.lib.common

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Uuids.uuid

class UuidsTest {

    // Tests

    @Test
    void shouldGenerateUuidString() {
        // Given
        def uuidString = uuid()

        // When
        def parsedUuid = UUID.fromString(uuidString)

        // Then
        assertThat(uuidString).isEqualTo(parsedUuid.toString())
    }

}
