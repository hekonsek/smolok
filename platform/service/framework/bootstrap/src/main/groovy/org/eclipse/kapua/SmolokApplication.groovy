package org.eclipse.kapua

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Boostraps Spring Boot application capable of connecting to the Smolok event bus. The application loads all Smolok
 * modules available in a classpath.
 */
@Configuration
@ComponentScan(['net.smolok', 'smolok'])
class SmolokApplication {
}