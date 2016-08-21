package smolok.lib.common

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.common.Uuids.uuid

class PropertiesTest {
	
	private static final def LOG = getLogger(Networks.class)
	

    @Test
    void shouldReadSystemProperty() {
        // Given
        def property = uuid()
        def value = uuid()
        System.setProperty(property, value)

        // When
        def valueRead = Properties.stringProperty(property)

        // Then
        assertThat(valueRead).isEqualTo(value)
    }

    @Test
    void shouldReadPropertyFromFile() {
        assertThat(Properties.stringProperty('fromApp')).isEqualTo('I am from app!')
    }

    @Test
    void shouldRestoreProperties() {
        // Given
        def property = 'property'
        Properties.setBooleanProperty(property, true)

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
		def valueRead = Properties.stringProperty(property)

		// Then
		assertThat(valueRead).isEqualTo(value)

		property = Uuids.uuid()
		value = Uuids.uuid()

		valueRead = Properties.setThreadStringProperty(property,value)
		LOG.debug(property + " " +value)

		// Then
		assertThat(valueRead).isEqualTo(value)

		def thread = Thread.start {
			def anotherThreadRead = Properties.stringProperty(property)
			LOG.debug(property + " " +anotherThreadRead)

			assertThat(null).isEqualTo(anotherThreadRead);

		}
		Thread.sleep(2000)
	}


	@Test
	void shouldReadIntThreadProperty() {
		// Given
		def property = Uuids.uuid()
		def value = 123

		LOG.debug(property + " " +value)

		Properties.setThreadIntProperty(property, value)

		// When
		def valueRead = Properties.intProperty(property)

		// Then
		assertThat(valueRead).isEqualTo(value)

		property = Uuids.uuid()
		value = 345

		valueRead = Properties.setThreadIntProperty(property,value)
		LOG.debug( property + " " +value)

		// Then
		assertThat(valueRead).isEqualTo(value)

		def thread = Thread.start {
			def anotherThreadRead = Properties.intProperty(property)
			LOG.debug(property + " " +anotherThreadRead)

			assertThat(null).isEqualTo(anotherThreadRead);
			
		}
		Thread.sleep(2000)
	}

}
