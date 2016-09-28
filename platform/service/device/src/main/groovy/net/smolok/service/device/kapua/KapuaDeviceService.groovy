package net.smolok.service.device.kapua

import net.smolok.service.device.api.Device
import net.smolok.service.device.api.DeviceService
import net.smolok.service.documentstore.api.QueryBuilder
import org.eclipse.kapua.service.device.registry.Device as KapuaDevice
import org.eclipse.kapua.service.device.registry.DeviceRegistryService
import org.eclipse.kapua.service.device.registry.KapuaEid
import org.eclipse.kapua.service.device.registry.mongodb.DocumentStoreDeviceQuery
import org.eclipse.kapua.service.device.registry.mongodb.SimpleDeviceCreator
import smolok.service.binding.Tenant

class KapuaDeviceService implements DeviceService {

    private final DeviceRegistryService kapuaService

    private final TenantMapper tenantMapper

    // Constructors

    KapuaDeviceService(DeviceRegistryService kapuaService, TenantMapper tenantMapper) {
        this.kapuaService = kapuaService
        this.tenantMapper = tenantMapper
    }

    // Operations

    @Override
    Device get(@Tenant String tenant, String deviceId) {
        def kapuaDevice = kapuaService.findByClientId(tenantId(tenant), deviceId)
        if(kapuaDevice == null) {
            return null
        }
        kapuaDeviceToDevice(kapuaDevice)
    }

    @Override
    List<Device> find(@Tenant String tenant, QueryBuilder queryBuilder) {
        if(queryBuilder.query.containsKey('deviceId')) {
            queryBuilder.query.put('clientId', queryBuilder.query.get('deviceId'))
            queryBuilder.query.remove('deviceId')
        }
        kapuaService.query(new DocumentStoreDeviceQuery(tenantId(tenant), queryBuilder)).collect{
            kapuaDeviceToDevice(it)
        }
    }

    @Override
    long count(@Tenant String tenant, QueryBuilder queryBuilder) {
        if(queryBuilder.query.containsKey('deviceId')) {
            queryBuilder.query.put('clientId', queryBuilder.query.get('deviceId'))
            queryBuilder.query.remove('deviceId')
        }
        kapuaService.count(new DocumentStoreDeviceQuery(tenantId(tenant), queryBuilder))
    }

    @Override
    void register(@Tenant String tenant, Device device) {
        def existingDevice = kapuaService.findByClientId(tenantId(tenant), device.deviceId)
        if(existingDevice != null) {
            kapuaService.update(existingDevice)
        } else {
            def deviceCreator = new SimpleDeviceCreator(tenantMapper.tenantMapping(tenant))
            deviceCreator.clientId = device.deviceId
            kapuaService.create(deviceCreator)
        }
    }

    @Override
    void update(Device device) {

    }

    @Override
    void deregister(@Tenant String tenant, String deviceId) {
        def existingDevice = kapuaService.findByClientId(tenantId(tenant), deviceId)
        kapuaService.delete(existingDevice.scopeId, existingDevice.id)
    }

    @Override
    void heartbeat(String deviceId) {

    }

    // Helpers

    private KapuaEid tenantId(String tenantName) {
        new KapuaEid(tenantMapper.tenantMapping(tenantName))
    }

    private Device kapuaDeviceToDevice(KapuaDevice kapuaDevice) {
        def device = new Device()
        device.deviceId = kapuaDevice.clientId
        device.properties.kapuaScopeId = kapuaDevice.scopeId.id
        device.properties.kapuaId = kapuaDevice.id.id
        device
    }

}
