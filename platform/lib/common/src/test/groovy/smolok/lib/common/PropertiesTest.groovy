package smolok.lib.common

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.Properties.booleanProperty
import static smolok.lib.common.Properties.intProperty
import static smolok.lib.common.Properties.setSystemBooleanProperty
import static smolok.lib.common.Properties.setThreadBooleanProperty
import static smolok.lib.common.Properties.stringProperty
import static smolok.lib.common.Uuids.uuid

class PropertiesTest {
	
	private static final def LOG = getLogger(Networks.class)

	// Tests

	@Test
	void shouldReturnSystemBooleanProperty() {
		// Given
		def property = uuid()
		def value = true
		setSystemBooleanProperty(property, value)

		// When
		def valueRead = booleanProperty(property)

		// Then
		assertThat(valueRead).contains(value)
	}

	@Test
	void shouldReturnThreadBooleanProperty() {
		// Given
		def property = uuid()
		def value = true
		setThreadBooleanProperty(property, value)

		// When
		def valueRead = booleanProperty(property)

		// Then
		assertThat(valueRead).contains(value)
	}

	@Test
	void shouldReturnEmptyBooleanProperty() {
		// Given
		def property = uuid()

		// When
		def valueRead = booleanProperty(property)

		// Then
		assertThat(valueRead).isEmpty()
	}

    @Test
    void shouldReadPropertyFromFile() {
        assertThat(stringProperty('fromApp')).isEqualTo('I am from app!')
    }

    @Test
    void shouldRestoreProperties() {
        // Given
        def property = 'property'
        setSystemBooleanProperty(property, true)

        // When
        Properties.restoreSystemProperties()

        // Then
        assertThat(Properties.booleanProperty(property, false)).isFalse()
    }
	
	@Test
	void shouldReadThreadProperty() {
		// Given
		def property = Uuids.uuid()
		def value = Uuids.uuid()

		LOG.debug(property + " " +value)

		Properties.setThreadStringProperty(property, value)

		// When
		def valueRead = stringProperty(property)

		// Then
		assertThat(valueRead).isEqualTo(value)
	}


	@Test
	void shouldReadIntThreadProperty() {
		// Given
		def property = uuid()
		def value = 123

		LOG.debug(property + " " +value)

		Properties.setThreadIntProperty(property, value)

		// When
		def valueRead = intProperty(property)

		// Then
		assertThat(valueRead).isEqualTo(value)
	}

}
