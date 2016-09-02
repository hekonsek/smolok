package net.smolok.service.device.api;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Represents information about device registered in a cloud service.
 */
public class Device {

    private String deviceId;

    private Date registrationDate;

    private Date lastUpdate;

    private boolean disconnected;

    private Map<String, Object> properties = new HashMap<>();

    // Constructors

    public Device() {
    }

    public Device(String deviceId, Date registrationDate, Date lastUpdate, boolean disconnected, Map<String, Object> properties) {
        this.deviceId = deviceId;
        this.registrationDate = registrationDate;
        this.lastUpdate = lastUpdate;
        this.disconnected = disconnected;
        this.properties = properties;
    }

    public static Device minimalDevice(String deviceId) {
        Device device = new Device();
        device.setDeviceId(deviceId);
        return device;
    }

    // Getters and setters


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}