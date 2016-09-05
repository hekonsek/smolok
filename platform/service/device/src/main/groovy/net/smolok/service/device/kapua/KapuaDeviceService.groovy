package net.smolok.service.device.kapua

import net.smolok.service.device.api.Device
import net.smolok.service.device.api.DeviceService
import net.smolok.service.device.api.QueryBuilder
import org.eclipse.kapua.service.device.registry.DeviceRegistryService

class KapuaDeviceService implements DeviceService {

    private final DeviceRegistryService kapuaService

    KapuaDeviceService(DeviceRegistryService kapuaService) {
        this.kapuaService = kapuaService
    }

    @Override
    Device get(String deviceId) {
        kapuaService.query(null)
    }

    @Override
    List<Device> findByQuery(QueryBuilder queryBuilder) {
        return null
    }

    @Override
    long countByQuery(QueryBuilder queryBuilder) {
        return 0
    }

    @Override
    void register(Device device) {

    }

    @Override
    void update(Device device) {

    }

    @Override
    void deregister(String deviceId) {

    }

    @Override
    void heartbeat(String deviceId) {

    }

}
