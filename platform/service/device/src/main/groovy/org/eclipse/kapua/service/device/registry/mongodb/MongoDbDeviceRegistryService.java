package org.eclipse.kapua.service.device.registry.mongodb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.mongodb.*;
import net.smolok.service.documentstore.api.DocumentStore;
import org.eclipse.kapua.service.device.registry.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class MongoDbDeviceRegistryService implements DeviceRegistryService {

    private final ObjectMapper objectMapper = new ObjectMapper().
            configure(FAIL_ON_UNKNOWN_PROPERTIES, false).
            setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final DocumentStore documentStore;

    private final MongoClient mongo;

    private final String db;

    private final String collection;

    // Constructors

    public MongoDbDeviceRegistryService(DocumentStore documentStore, MongoClient mongo, String db, String collection) {
        this.documentStore = documentStore;
        this.mongo = mongo;
        this.db = db;
        this.collection = collection;
    }

    // API operations

    @Override
    public Device create(DeviceCreator deviceCreator) throws KapuaException {
        long id = new Random().nextLong();

        DBObject device = new BasicDBObject();
        device.put("scopeId", deviceCreator.getScopeId().getId().longValue());
        device.put("kapuaid", id);
        device.put("clientId", deviceCreator.getClientId());
        documentStore.save(collection, objectMapper.convertValue(device, Map.class));

        SimpleDevice result = new SimpleDevice();
        result.setScopeId(deviceCreator.getScopeId().getId());
        result.setId(BigInteger.valueOf(id));
        return result;
    }

    @Override
    public Device update(Device device) throws KapuaException {
        DBCursor devices = devicesCollection().find(deviceId(device.getScopeId(), device.getId()));
        DBObject existingDevice = devices.next();
        Map<String, Object> existingDeviceMap = existingDevice.toMap();
        existingDeviceMap.putAll(objectMapper.convertValue(device, Map.class));
        normalize(existingDeviceMap);
        existingDeviceMap.put("id", existingDeviceMap.get("_id").toString());
        existingDeviceMap.remove("_id");
        documentStore.save(collection, existingDeviceMap);
        return new SimpleDevice();
    }

    @Override
    public Device find(KapuaId scopeId, KapuaId entityId) throws KapuaException {
        DBCursor devices = devicesCollection().find(deviceId(scopeId, entityId));
        if (devices.hasNext()) {
            return dbObjectToDevice(devices.next());
        }
        return null;
    }

    @Override
    public DeviceListResult query(KapuaQuery<Device> query) throws KapuaException {
        DBCursor devicesRecords = devicesCollection().find();
        DeviceListResult devices = new DeviceListResultImpl();
        while (devicesRecords.hasNext()) {
            devices.add(dbObjectToDevice(devicesRecords.next()));
        }
        return devices;
    }

    @Override
    public long count(KapuaQuery<Device> query) throws KapuaException {
        return query(query).size();
    }

    @Override
    public void delete(Device device) throws KapuaException {
        devicesCollection().remove(deviceId(device.getScopeId(), device.getId()));
    }

    @Override
    public Device findByClientId(KapuaId scopeId, String clientId) throws KapuaException {
        DBCursor devices = devicesCollection().find(new BasicDBObject(ImmutableMap.of("scopeId", scopeId.getId().longValue(), "clientId", clientId)));
        if (devices.hasNext()) {
            return dbObjectToDevice(devices.next());
        }
        return null;
    }

    // Helpers

    private DBCollection devicesCollection() {
        return mongo.getDB(db).getCollection(collection);
    }

    private DBObject deviceId(KapuaId scopeId, KapuaId entityId) {
        return new BasicDBObject(ImmutableMap.of("scopeId", scopeId.getId().longValue(), "kapuaid", entityId.getId().longValue()));
    }

    private Device dbObjectToDevice(DBObject dbObject) {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.putAll(dbObject.toMap());
        deviceMap.put("id", deviceMap.get("kapuaid"));
        deviceMap.remove("kapuaid");
        return objectMapper.convertValue(deviceMap, SimpleDevice.class);
    }

    private void normalize(Map<String, Object> device) {
        for (String key : device.keySet()) {
            if (key.equals("id") || key.equals("scopeId")) {
                Map<String, Object> value = (Map<String, Object>) device.get(key);
                device.put(key, ((BigInteger) value.get("id")).longValue());
            }
        }
    }

}
