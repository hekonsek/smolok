package smolok.lib.spark.job

import org.junit.BeforeClass
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static RddJobContext.OPTION_TESTING
import static RddJobContext.enableTesting
import static smolok.lib.common.Properties.setSystemStringProperty

class RddJobContextTest {

    @BeforeClass
    static void beforeClass() {
        enableTesting()
    }

    // Tests

    @Test
    void shouldEnableTesting() {
        def testing = new RddJobContext().option(OPTION_TESTING)
        assertThat(testing).is('true')
    }

    @Test
    void shouldLoadTestFile() {
        def job = new RddJobContext()
        def count = job.source('text-file:/var/data/foo.txt').count()
        assertThat(count).isEqualTo(3)
    }

    @Test
    void shouldCreateNewList() {
        def job = new RddJobContext()
        def count = job.source('list:1,2,3,4,5').count()
        assertThat(count).isEqualTo(5)
    }

    @Test
    void shouldLoadSourceFromSystemProperty() {
        // Given
        setSystemStringProperty('mysource', 'list:1,2,3,4,5')
        def job = new RddJobContext()

        // When
        def count = job.source('mysource').count()

        // Then
        assertThat(count).isEqualTo(5)
    }

}
