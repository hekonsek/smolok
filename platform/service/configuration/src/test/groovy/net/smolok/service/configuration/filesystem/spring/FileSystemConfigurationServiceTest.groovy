package net.smolok.service.configuration.filesystem.spring

import net.smolok.service.configuration.api.ConfigurationService
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static java.io.File.createTempFile
import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Properties.setSystemStringProperty
import static smolok.lib.common.Uuids.uuid

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KapuaApplication.class)
class FileSystemConfigurationServiceTest {

    def key = uuid()

    def value = uuid()

    @Autowired
    ConfigurationService configurationServiceClient

    @BeforeClass
    static void beforeClass() {
        setSystemStringProperty('configuration.file', createTempFile('smolok', 'test').absolutePath)
    }

    // Tests

    @Test
    void shouldReadStoredProperty() {
        // Given
        configurationServiceClient.put(key, value)

        // When
        def property = configurationServiceClient.get(key)

        // Then
        assertThat(property).isEqualTo(value)
    }

    @Test
    void shouldOverrideProperty() {
        // Given
        configurationServiceClient.put(key, value)
        configurationServiceClient.put(key, 'newValue')

        // When
        def property = configurationServiceClient.get(key)

        // Then
        assertThat(property).isEqualTo('newValue')
    }

}