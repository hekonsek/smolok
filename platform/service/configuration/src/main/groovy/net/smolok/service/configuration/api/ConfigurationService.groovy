package net.smolok.service.configuration.api

interface ConfigurationService {

    String get(String key)

    void put(String key, String value)

}