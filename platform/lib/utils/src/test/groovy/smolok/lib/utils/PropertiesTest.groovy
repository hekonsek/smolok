package smolok.lib.utils

import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static smolok.lib.utils.Properties.booleanProperty
import static smolok.lib.utils.Properties.restoreSystemProperties
import static smolok.lib.utils.Properties.setBooleanProperty
import static smolok.lib.utils.Properties.stringProperty
import static smolok.lib.utils.Properties.setThreadIntProperty
import static smolok.lib.utils.Properties.setThreadStringProperty
import static smolok.lib.utils.Properties.intProperty

import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.utils.Uuids.uuid;


class PropertiesTest {
	
	private static final def LOG = getLogger(Networks.class)
	

    @Test
    void shouldReadSystemProperty() {
        // Given
        def property = uuid()
        def value = uuid()
        System.setProperty(property, value)

        // When
        def valueRead = stringProperty(property)

        // Then
        assertThat(valueRead).isEqualTo(value)
    }

    @Test
    void shouldReadPropertyFromFile() {
        assertThat(stringProperty('fromApp')).isEqualTo('I am from app!')
    }

    @Test
    void shouldRestoreProperties() {
        // Given
        def property = 'property'
        setBooleanProperty(property, true)

        // When
        restoreSystemProperties()

        // Then
        assertThat(booleanProperty(property, false)).isFalse()
    }
	
	@Test
	void shouldReadThreadProperty() {
		// Given
		def property = uuid()
		def value = uuid()

		LOG.debug(property + " " +value)

		setThreadStringProperty(property, value)

		// When
		def valueRead = stringProperty(property)

		// Then
		assertThat(valueRead).isEqualTo(value)

		property = uuid()
		value = uuid()

		valueRead = setThreadStringProperty(property,value)
		LOG.debug(property + " " +value)

		// Then
		assertThat(valueRead).isEqualTo(value)

		def thread = Thread.start {
			def anotherThreadRead = stringProperty(property)
			LOG.debug(property + " " +anotherThreadRead)

			assertThat(null).isEqualTo(anotherThreadRead);

		}
		Thread.sleep(2000)
	}


	@Test
	void shouldReadIntThreadProperty() {
		// Given
		def property = uuid()
		def value = 123

		LOG.debug(property + " " +value)

		setThreadIntProperty(property, value)

		// When
		def valueRead = intProperty(property)

		// Then
		assertThat(valueRead).isEqualTo(value)

		property = uuid()
		value = 345

		valueRead = setThreadIntProperty(property,value)
		LOG.debug( property + " " +value)

		// Then
		assertThat(valueRead).isEqualTo(value)

		def thread = Thread.start {
			def anotherThreadRead = intProperty(property)
			LOG.debug(property + " " +anotherThreadRead)

			assertThat(null).isEqualTo(anotherThreadRead);
			
		}
		Thread.sleep(2000)
	}

}
