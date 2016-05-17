package smolok.lib.common

import groovy.transform.Immutable

import java.nio.file.Paths

import static java.lang.String.format
import static org.apache.commons.lang3.SystemUtils.USER_HOME
import static org.slf4j.LoggerFactory.getLogger

public final class Mavens {

    private static final VERSIONS = new Properties()

    private static final LOG = getLogger(Mavens.class)

    private static final String DEPENDENCIES_PROPERTIES_PATH = "META-INF/maven/dependencies.properties";

    static {
        try {
            Enumeration<URL> dependenciesPropertiesStreams = Mavens.class.getClassLoader().getResources(DEPENDENCIES_PROPERTIES_PATH);
            if(!dependenciesPropertiesStreams.hasMoreElements()) {
                LOG.debug(format("No %s file found in the classpath.", DEPENDENCIES_PROPERTIES_PATH))
            }
            while (dependenciesPropertiesStreams.hasMoreElements()) {
                InputStream propertiesStream = dependenciesPropertiesStreams.nextElement().openStream();
                LOG.debug("Loading properties: " + propertiesStream);
                VERSIONS.load(propertiesStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Constructors

    private Mavens() {
    }

    /**
     * Returns local Maven repository.
     * 
     * @return {@code java.io.File} pointing to the local Maven repository.
     */
    static File localMavenRepository() {
        Paths.get(USER_HOME, '.m2', 'repository').toFile()
    }

    static String artifactVersionFromDependenciesProperties(String groupId, String artifactId) {
        VERSIONS.getProperty(format("%s/%s/version", groupId, artifactId))
    }

    // Static classes

    @Immutable
    static class MavenCoordinates {

        public static final def DEFAULT_COORDINATES_SEPARATOR = ':'

        String groupId

        String artifactId

        String version

        static MavenCoordinates parseMavenCoordinates(String coordinates) {
            parseMavenCoordinates(coordinates, DEFAULT_COORDINATES_SEPARATOR)
        }

        static MavenCoordinates parseMavenCoordinates(String coordinates, String separator) {
            def parsedCoordinates = coordinates.split(separator)
            new MavenCoordinates(parsedCoordinates[0], parsedCoordinates[1], parsedCoordinates[2]);
        }

    }

}