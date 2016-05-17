package smolok.lib.common

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Mavens.MavenCoordinates.parseMavenCoordinates
import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties

class MavensTest {

    @Test
    void shouldParseCoordinates() {
        // Given
        def group = 'foo'
        def artifact = 'bar'
        def version = '1'
        def coordinatesString = "${group}:${artifact}:${version}"

        // When
        def coordinates = parseMavenCoordinates(coordinatesString)

        // Then
        assertThat(coordinates.artifactId).isEqualTo(artifact)
        assertThat(coordinates.groupId).isEqualTo(group)
        assertThat(coordinates.version).isEqualTo(version)
    }

    @Test
    void shouldParseCoordinatesWithCustomSeparator() {
        // Given
        def group = 'foo'
        def artifact = 'bar'
        def version = '1'
        def coordinatesString = "${group}/${artifact}/${version}"

        // When
        def coordinates = parseMavenCoordinates(coordinatesString, '/')

        // Then
        assertThat(coordinates.artifactId).isEqualTo(artifact)
        assertThat(coordinates.groupId).isEqualTo(group)
        assertThat(coordinates.version).isEqualTo(version)
    }

    @Test
    void shouldLoadDependencyVersion() {
        def version = artifactVersionFromDependenciesProperties('com.test', 'test')
        assertThat(version).isEqualTo('6.6.6')
    }

}
