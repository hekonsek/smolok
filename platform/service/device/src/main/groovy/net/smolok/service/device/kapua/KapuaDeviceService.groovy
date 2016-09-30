/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.smolok.service.device.kapua

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import net.smolok.service.device.api.Device
import net.smolok.service.device.api.DeviceService
import net.smolok.service.documentstore.api.QueryBuilder
import org.eclipse.kapua.service.device.registry.Device as KapuaDevice
import org.eclipse.kapua.service.device.registry.DeviceRegistryService
import org.eclipse.kapua.service.device.registry.KapuaEid
import org.eclipse.kapua.service.device.registry.mongodb.DocumentStoreDeviceQuery
import org.eclipse.kapua.service.device.registry.mongodb.SimpleDevice
import org.eclipse.kapua.service.device.registry.mongodb.SimpleDeviceCreator
import smolok.service.binding.Tenant

import static smolok.lib.common.Lang.nullOr

class KapuaDeviceService implements DeviceService {

    def mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)

    // Collaborators

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
        nullOr(kapuaService.findByClientId(tenantId(tenant), deviceId)) { kapuaDeviceToDevice(it) }
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
            kapuaService.update(updateDevice(existingDevice, device))
        } else {
            def deviceCreator = new SimpleDeviceCreator(tenantMapper.tenantMapping(tenant))
            deviceCreator.clientId = device.deviceId
            kapuaService.create(deviceCreator)
        }
    }

    @Override
    void update(@Tenant String tenant, Device device) {
        def existingDevice = kapuaService.findByClientId(tenantId(tenant), device.deviceId)
        if(existingDevice != null) {
            kapuaService.update(updateDevice(existingDevice, device))
        }
    }

    @Override
    void deregister(@Tenant String tenant, String deviceId) {
        def existingDevice = kapuaService.findByClientId(tenantId(tenant), deviceId)
        kapuaService.delete(existingDevice.scopeId, existingDevice.id)
    }

    @Override
    void heartbeat(@Tenant String tenant, String deviceId) {

    }

    // Helpers

    private KapuaEid tenantId(String tenantName) {
        new KapuaEid(tenantMapper.tenantMapping(tenantName))
    }

    private Device kapuaDeviceToDevice(KapuaDevice kapuaDevice) {
        def device = new Device()
        device.deviceId = kapuaDevice.clientId
        device.properties = kapuaDevice.properties
        device.properties.kapuaScopeId = kapuaDevice.scopeId.id
        device.properties.kapuaId = kapuaDevice.id.id

        device.registrationDate = kapuaDevice.createdOn
        device.lastUpdate = kapuaDevice.lastEventOn

        device
    }

    private KapuaDevice deviceToKapuaDevice(Device device) {
        def kapuaDevice = new SimpleDevice()
        kapuaDevice.clientId = device.deviceId

        kapuaDevice.scopeId = device.properties.kapuaScopeId
        kapuaDevice.id = device.properties.kapuaId
        kapuaDevice.properties = device.properties
        kapuaDevice.properties.remove('kapuaScopeId')
        kapuaDevice.properties.remove('kapuaId')

        kapuaDevice.createdOn = device.registrationDate
        kapuaDevice.lastEventOn = device.lastUpdate

        kapuaDevice
    }

    private KapuaDevice updateDevice(KapuaDevice existingDevice, Device device) {
        def ex = mapper.convertValue(existingDevice, Map.class)
        ex.putAll(mapper.convertValue(deviceToKapuaDevice(device), Map.class))
        ex.id = ex.id.id
        ex.scopeId = ex.scopeId.id
        mapper.convertValue(ex, SimpleDevice.class)
    }

}
