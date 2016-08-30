package net.smolok.service.configuration.filesystem

import net.smolok.service.configuration.api.ConfigurationService

class FileSystemConfigurationService implements ConfigurationService {

    private final File root

    FileSystemConfigurationService(File root) {
        this.root = root

        root.createNewFile()
    }

    @Override
    String get(String key) {
        def properties = new Properties()
        properties.load(root.newInputStream())
        properties.getProperty(key)
    }

    @Override
    void put(String key, String value) {
        def properties = new Properties()
        properties.load(root.newInputStream())
        properties.setProperty(key, value)
        properties.store(root.newOutputStream(), '')
    }

}
