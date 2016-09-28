package org.eclipse.kapua.service.device.registry.mongodb;

import org.eclipse.kapua.service.device.registry.*;

import java.math.BigInteger;
import java.util.Date;
import java.util.Properties;

public class SimpleDevice implements Device {

    private String clientId;

    private String type;

    private DeviceStatus status;

    private BigInteger scopeId;

    private BigInteger kapuaId;

    private String displayName;

    private Date createdOn;

    private BigInteger createdBy;

    private Date lastEventOn;

    private Date modifiedOn;

    private BigInteger modifiedBy;

    private DeviceEventType lastEventType;

    private String serialNumber;

    private String modelId;

    private String imei;

    private String imsi;

    private String iccid;

    private String biosVersion;

    private String firmwareVersion;

    private String osVersion;

    private String jvmVersion;

    private String osgiFrameworkVersion;

    private String applicationFrameworkVersion;

    private String applicationIdentifiers;

    private String acceptEncoding;

    private Double gpsLongitude;

    private Double gpsLatitude;

    private String customAttribute1;

    private String customAttribute2;

    private String customAttribute3;

    private String customAttribute4;

    private String customAttribute5;

    private DeviceCredentialsMode credentialsMode;

    private KapuaId deviceUserId;

    private int optlock;

    private Properties entityProperties;

    private Properties entityAttributes;

    private BigInteger preferredUserId;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public Date getLastEventOn() {
        return lastEventOn;
    }

    @Override
    public void setLastEventOn(Date lastEventOn) {
        this.lastEventOn = lastEventOn;
    }

    @Override
    public DeviceEventType getLastEventType() {
        return lastEventType;
    }

    @Override
    public void setLastEventType(DeviceEventType lastEventType) {
        this.lastEventType = lastEventType;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public String getModelId() {
        return modelId;
    }

    @Override
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public Date getModifiedOn() {
        return modifiedOn;
    }

    @Override
    public KapuaId getModifiedBy() {
        return modifiedBy != null ? new KapuaEid(modifiedBy) : null;
    }

    public void setModifiedBy(KapuaId modifiedBy) {
        if(modifiedBy != null) {
            this.modifiedBy = modifiedBy.getId();
        }
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @Override
    public KapuaId getCreatedBy() {
        return createdBy != null ? new KapuaEid(createdBy) : null;
    }

    public void setCreatedBy(KapuaId createdBy) {
        if(createdBy != null) {
            this.createdBy = createdBy.getId();
        }
    }

    @Override
    public String getImei() {
        return imei;
    }

    @Override
    public void setImei(String imei) {
        this.imei = imei;
    }

    @Override
    public String getImsi() {
        return imsi;
    }

    @Override
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    @Override
    public String getIccid() {
        return iccid;
    }

    @Override
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    @Override
    public String getBiosVersion() {
        return biosVersion;
    }

    @Override
    public void setBiosVersion(String biosVersion) {
        this.biosVersion = biosVersion;
    }

    @Override
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    @Override
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    @Override
    public String getOsVersion() {
        return osVersion;
    }

    @Override
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    @Override
    public String getJvmVersion() {
        return jvmVersion;
    }

    @Override
    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    @Override
    public String getOsgiFrameworkVersion() {
        return osgiFrameworkVersion;
    }

    @Override
    public void setOsgiFrameworkVersion(String osgiFrameworkVersion) {
        this.osgiFrameworkVersion = osgiFrameworkVersion;
    }

    public String getApplicationFrameworkVersion() {
        return applicationFrameworkVersion;
    }

    public void setApplicationFrameworkVersion(String applicationFrameworkVersion) {
        this.applicationFrameworkVersion = applicationFrameworkVersion;
    }

    @Override
    public String getApplicationIdentifiers() {
        return applicationIdentifiers;
    }

    @Override
    public void setApplicationIdentifiers(String applicationIdentifiers) {
        this.applicationIdentifiers = applicationIdentifiers;
    }

    @Override
    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    @Override
    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    @Override
    public Double getGpsLongitude() {
        return gpsLongitude;
    }

    @Override
    public void setGpsLongitude(Double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    @Override
    public Double getGpsLatitude() {
        return gpsLatitude;
    }

    @Override
    public void setGpsLatitude(Double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    @Override
    public String getCustomAttribute1() {
        return customAttribute1;
    }

    @Override
    public void setCustomAttribute1(String customAttribute1) {
        this.customAttribute1 = customAttribute1;
    }

    @Override
    public String getCustomAttribute2() {
        return customAttribute2;
    }

    @Override
    public void setCustomAttribute2(String customAttribute2) {
        this.customAttribute2 = customAttribute2;
    }

    @Override
    public String getCustomAttribute3() {
        return customAttribute3;
    }

    @Override
    public void setCustomAttribute3(String customAttribute3) {
        this.customAttribute3 = customAttribute3;
    }

    @Override
    public String getCustomAttribute4() {
        return customAttribute4;
    }

    @Override
    public void setCustomAttribute4(String customAttribute4) {
        this.customAttribute4 = customAttribute4;
    }

    @Override
    public String getCustomAttribute5() {
        return customAttribute5;
    }

    @Override
    public void setCustomAttribute5(String customAttribute5) {
        this.customAttribute5 = customAttribute5;
    }

    @Override
    public DeviceCredentialsMode getCredentialsMode() {
        return credentialsMode;
    }

    @Override
    public void setCredentialsMode(DeviceCredentialsMode credentialsMode) {
        this.credentialsMode = credentialsMode;
    }

    public KapuaId getDeviceUserId() {
        return deviceUserId;
    }

    public void setDeviceUserId(KapuaId deviceUserId) {
        this.deviceUserId = deviceUserId;
    }

    @Override
    public int getOptlock() {
        return optlock;
    }

    @Override
    public void setOptlock(int optlock) {
        this.optlock = optlock;
    }

    @Override
    public Properties getEntityProperties() {
        return entityProperties;
    }

    @Override
    public void setEntityProperties(Properties entityProperties) {
        this.entityProperties = entityProperties;
    }

    @Override
    public Properties getEntityAttributes() {
        return entityAttributes;
    }

    @Override
    public void setEntityAttributes(Properties entityAttributes) {
        this.entityAttributes = entityAttributes;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public DeviceStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    void setId(BigInteger kapuaId) {
        this.kapuaId = kapuaId;
    }

    @Override
    public KapuaId getId() {
        return kapuaId != null ? new KapuaEid(kapuaId) : null;
    }

    public void setScopeId(BigInteger scopeId) {
        this.scopeId = scopeId;
    }

    @Override
    public KapuaId getScopeId() {
        return scopeId != null ? new KapuaEid(scopeId) : null;
    }

    @Override
    public KapuaId getPreferredUserId() {
        return preferredUserId != null ? new KapuaEid(preferredUserId) : null;
    }

    public void setPreferredUserId(KapuaId preferredUserId) {
        if(preferredUserId != null) {
            this.preferredUserId = preferredUserId.getId();
        }
    }

}
