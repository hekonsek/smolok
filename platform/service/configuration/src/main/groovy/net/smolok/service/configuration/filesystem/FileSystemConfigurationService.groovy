package net.smolok.service.configuration.filesystem

import net.smolok.service.configuration.api.ConfigurationService
import org.apache.commons.lang3.Validate

import static smolok.lib.common.Lang.nullOr

class FileSystemConfigurationService implements ConfigurationService {

    private final File root

    FileSystemConfigurationService(File root) {
        this.root = root

        Validate.isTrue(root.parentFile.exists() || root.parentFile.mkdirs(), "Cannot create parent directory for configuration: ${root.parent}")
        root.createNewFile()
    }

    @Override
    String get(String key) {
        synchronized (this) {
            def propertiesInputStream = root.newInputStream()
            try {
                def properties = new Properties()
                properties.load(propertiesInputStream)
                properties.getProperty(key)
            } finally {
                nullOr(propertiesInputStream) { it.close() }
            }
        }
    }

    @Override
    void put(String key, String value) {
        synchronized (this) {
            def properties = new Properties()
            properties.load(root.newInputStream())
            properties.setProperty(key, value)
            properties.store(root.newOutputStream(), '')
        }
    }

}
