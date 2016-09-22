package net.smolok.service.device.kapua

import net.smolok.service.device.api.Device
import net.smolok.service.device.api.DeviceService
import net.smolok.service.documentstore.api.QueryBuilder
import org.eclipse.kapua.service.device.registry.DeviceRegistryService
import org.eclipse.kapua.service.device.registry.KapuaEid
import org.eclipse.kapua.service.device.registry.mongodb.DocumentStoreDeviceQuery
import org.eclipse.kapua.service.device.registry.mongodb.SimpleDeviceCreator
import smolok.service.binding.Tenant

class KapuaDeviceService implements DeviceService {

    private final DeviceRegistryService kapuaService

    KapuaDeviceService(DeviceRegistryService kapuaService) {
        this.kapuaService = kapuaService
    }

    @Override
    Device get(@Tenant String tenant, String deviceId) {
        def kapuaDevice = kapuaService.findByClientId(new KapuaEid(tenant.hashCode().toBigInteger()), deviceId)
        if(kapuaDevice == null) {
            return null
        }
        def device = new Device()
        device.deviceId = kapuaDevice.clientId
        device.properties.kapuaScopeId = kapuaDevice.scopeId.id
        device.properties.kapuaId = kapuaDevice.id.id
        device
    }

    @Override
    List<Device> find(QueryBuilder queryBuilder) {
        kapuaService.query(queryBuilder)
    }

    @Override
    long count(@Tenant String tenant, QueryBuilder queryBuilder) {
        if(queryBuilder.query.containsKey('deviceId')) {
            queryBuilder.query.put('clientId', queryBuilder.query.get('deviceId'))
            queryBuilder.query.remove('deviceId')
        }
        kapuaService.count(new DocumentStoreDeviceQuery(new KapuaEid(tenant.hashCode().toBigInteger()), queryBuilder))
    }

    @Override
    void register(@Tenant String tenant, Device device) {
        def existingDevice = kapuaService.findByClientId(new KapuaEid(tenant.hashCode().toBigInteger()), device.deviceId)
        if(existingDevice != null) {
            kapuaService.update(existingDevice)
        } else {
            def deviceCreator = new SimpleDeviceCreator(tenant.hashCode().toBigInteger())
            deviceCreator.clientId = device.deviceId
            kapuaService.create(deviceCreator)
        }
    }

    @Override
    void update(Device device) {

    }

    @Override
    void deregister(@Tenant String tenant, String deviceId) {
        def existingDevice = kapuaService.findByClientId(new KapuaEid(tenant.hashCode().toBigInteger()), deviceId)
        kapuaService.delete(existingDevice.scopeId, existingDevice.id)
    }

    @Override
    void heartbeat(String deviceId) {

    }

}
