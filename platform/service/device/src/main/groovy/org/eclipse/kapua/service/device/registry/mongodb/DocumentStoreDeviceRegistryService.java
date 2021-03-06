package org.eclipse.kapua.service.device.registry.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.mongodb.*;
import net.smolok.service.documentstore.api.DocumentStore;
import net.smolok.service.documentstore.api.QueryBuilder;
import org.eclipse.kapua.service.device.registry.*;

import java.math.BigInteger;
import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class DocumentStoreDeviceRegistryService implements DeviceRegistryService {

    private final ObjectMapper objectMapper = new ObjectMapper().
            configure(FAIL_ON_UNKNOWN_PROPERTIES, false).
            setSerializationInclusion(NON_NULL);

    private final DocumentStore documentStore;

    private final Mongo mongo;

    private final String db;

    private final String collection;

    // Constructors

    public DocumentStoreDeviceRegistryService(DocumentStore documentStore, Mongo mongo, String db, String collection) {
        this.documentStore = documentStore;
        this.mongo = mongo;
        this.db = db;
        this.collection = collection;
    }

    // API operations

    @Override
    public Device create(DeviceCreator deviceCreator) throws KapuaException {
        long id = new Random().nextLong();

        Map<String,Object> device = new HashMap<>();
        device.put("kapuaid", id);
        device.put("clientId", deviceCreator.getClientId());

        device.put("createdOn", new Date());
        device.put("lastEventOn", new Date());

        documentStore.save(tenantCollection(deviceCreator.getScopeId()), objectMapper.convertValue(device, Map.class));

        SimpleDevice result = new SimpleDevice();
        result.setScopeId(deviceCreator.getScopeId().getId());
        result.setId(BigInteger.valueOf(id));
        return result;
    }

    @Override
    public Device update(Device device) throws KapuaException {
        List<Map<String, Object>> existingDevices = documentStore.find(tenantCollection(device.getScopeId()), new QueryBuilder(ImmutableMap.of("kapuaid", device.getId().getId().longValue())));
        if(existingDevices.isEmpty()) {
            return null;
        }

        Map<String, Object> existingDeviceMap = existingDevices.get(0);

        existingDeviceMap.put("lastEventOn", new Date());

        String documentId = existingDeviceMap.get("id").toString();
        existingDeviceMap.put("clientId", device.getClientId());
        existingDeviceMap.putAll(objectMapper.convertValue(device, Map.class));
        existingDeviceMap.put("id", documentId);
        Map<String, Object> value = (Map<String, Object>) existingDeviceMap.get("scopeId");
        existingDeviceMap.put("scopeId", ((BigInteger) value.get("id")).longValue());

        documentStore.save(tenantCollection(device.getScopeId()), existingDeviceMap);
        return new SimpleDevice();
    }

    @Override
    public Device find(KapuaId scopeId, KapuaId entityId) throws KapuaException {
        List<Map<String, Object>> devices = documentStore.find(tenantCollection(scopeId), new QueryBuilder(ImmutableMap.of("kapuaid", entityId.getId().longValue())));
        if (devices.isEmpty()) {
            return null;
        }
        return mapToDevice(scopeId, devices.get(0));
    }

    @Override
    public DeviceListResult query(KapuaQuery<Device> query) throws KapuaException {
        if(query.getPredicate() instanceof AttributePredicate) {
            AttributePredicate attributePredicate = (AttributePredicate) query.getPredicate();
            List<Map<String, Object>> devices = documentStore.find(tenantCollection(query.getScopeId()), new QueryBuilder(ImmutableMap.of(attributePredicate.getAttributeName(), attributePredicate.getAttributeValue())));
            DeviceListResult result = new DeviceListResultImpl();
            devices.forEach(device -> result.add(mapToDevice(query.getScopeId(), device)));
            return result;
        } else if(query instanceof DocumentStoreDeviceQuery) {
            List<Map<String, Object>> devices = documentStore.find(tenantCollection(query.getScopeId()), ((DocumentStoreDeviceQuery) query).queryBuilder());
            DeviceListResult result = new DeviceListResultImpl();
            devices.forEach(device -> result.add(mapToDevice(query.getScopeId(), device)));
            return result;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public long count(KapuaQuery<Device> query) throws KapuaException {
        return query(query).size();
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId deviceId) throws KapuaException {
        List<Map<String, Object>> devices = documentStore.find(tenantCollection(scopeId), new QueryBuilder(ImmutableMap.of("kapuaid", deviceId.getId().longValue())));
        if (devices.isEmpty()) {
            throw new IllegalArgumentException();
        }
        documentStore.remove(tenantCollection(scopeId), devices.get(0).get("id").toString());
    }

    @Override
    public Device findByClientId(KapuaId scopeId, String clientId) throws KapuaException {
        DBCursor devices = devicesCollection(scopeId).find(new BasicDBObject(ImmutableMap.of("clientId", clientId)));
        if (devices.hasNext()) {
            return dbObjectToDevice(scopeId, devices.next());
        }
        return null;
    }

    // Helpers

    private String tenantCollection(KapuaId scopeId) {
        return collection + "_" + scopeId.getId();
    }

    private DBCollection devicesCollection(KapuaId scopeId) {
        return mongo.getDB(db).getCollection(tenantCollection(scopeId));
    }

    private Device dbObjectToDevice(KapuaId scopeId, DBObject dbObject) {
        return mapToDevice(scopeId, dbObject.toMap());
    }

    private Device mapToDevice(KapuaId scopeId, Map<String, Object> dbObject) {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.putAll(dbObject);
        deviceMap.put("id", deviceMap.get("kapuaid"));
        deviceMap.remove("kapuaid");
        deviceMap.put("scopeId", scopeId.getId());
        return objectMapper.convertValue(deviceMap, SimpleDevice.class);
    }

}
