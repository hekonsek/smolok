package smolok.lib.common

import org.junit.Assert
import org.junit.Test

import static smolok.lib.common.Uuids.uuid

class UuidsTest extends Assert {

    @Test
    void shouldGenerateUuidString() {
        // Given
        def uuidString = uuid()

        // When
        def parsedUuid = UUID.fromString(uuidString)

        // Then
        assertEquals(uuidString, parsedUuid.toString())
    }

}
