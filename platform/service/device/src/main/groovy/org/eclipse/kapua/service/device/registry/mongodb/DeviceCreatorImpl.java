package org.eclipse.kapua.service.device.registry.mongodb;

import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceCredentialsMode;
import org.eclipse.kapua.service.device.registry.KapuaEid;
import org.eclipse.kapua.service.device.registry.KapuaId;

import java.math.BigInteger;

public class DeviceCreatorImpl implements DeviceCreator {

    private final BigInteger scopeId;

    public DeviceCreatorImpl(BigInteger scopeId) {
        this.scopeId = scopeId;
    }

    @Override
    public String getClientId() {
        return null;
    }

    @Override
    public void setClientId(String clientId) {

    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void setDisplayName(String displayName) {

    }

    @Override
    public String getSerialNumber() {
        return null;
    }

    @Override
    public void setSerialNumber(String serialNumber) {

    }

    @Override
    public String getModelId() {
        return null;
    }

    @Override
    public void setModelId(String modelId) {

    }

    @Override
    public String getImei() {
        return null;
    }

    @Override
    public void setImei(String imei) {

    }

    @Override
    public String getImsi() {
        return null;
    }

    @Override
    public void setImsi(String imsi) {

    }

    @Override
    public String getIccid() {
        return null;
    }

    @Override
    public void setIccid(String iccid) {

    }

    @Override
    public String getBiosVersion() {
        return null;
    }

    @Override
    public void setBiosVersion(String biosVersion) {

    }

    @Override
    public String getFirmwareVersion() {
        return null;
    }

    @Override
    public void setFirmwareVersion(String firmwareVersion) {

    }

    @Override
    public String getOsVersion() {
        return null;
    }

    @Override
    public void setOsVersion(String osVersion) {

    }

    @Override
    public String getJvmVersion() {
        return null;
    }

    @Override
    public void setJvmVersion(String jvmVersion) {

    }

    @Override
    public String getOsgiFrameworkVersion() {
        return null;
    }

    @Override
    public void setOsgiFrameworkVersion(String osgiFrameworkVersion) {

    }

    @Override
    public String getApplicationFrameworkVersion() {
        return null;
    }

    @Override
    public void setApplicationFrameworkVersion(String appFrameworkVersion) {

    }

    @Override
    public String getApplicationIdentifiers() {
        return null;
    }

    @Override
    public void setApplicationIdentifiers(String applicationIdentifiers) {

    }

    @Override
    public String getAcceptEncoding() {
        return null;
    }

    @Override
    public void setAcceptEncoding(String acceptEncoding) {

    }

    @Override
    public Double getGpsLongitude() {
        return null;
    }

    @Override
    public void setGpsLongitude(Double gpsLongitude) {

    }

    @Override
    public Double getGpsLatitude() {
        return null;
    }

    @Override
    public void setGpsLatitude(Double gpsLatitude) {

    }

    @Override
    public String getCustomAttribute1() {
        return null;
    }

    @Override
    public void setCustomAttribute1(String customAttribute1) {

    }

    @Override
    public String getCustomAttribute2() {
        return null;
    }

    @Override
    public void setCustomAttribute2(String customAttribute2) {

    }

    @Override
    public String getCustomAttribute3() {
        return null;
    }

    @Override
    public void setCustomAttribute3(String customAttribute3) {

    }

    @Override
    public String getCustomAttribute4() {
        return null;
    }

    @Override
    public void setCustomAttribute4(String customAttribute4) {

    }

    @Override
    public String getCustomAttribute5() {
        return null;
    }

    @Override
    public void setCustomAttribute5(String customAttribute5) {

    }

    @Override
    public DeviceCredentialsMode getCredentialsMode() {
        return null;
    }

    @Override
    public void setCredentialsMode(DeviceCredentialsMode credentialsMode) {

    }

    @Override
    public KapuaId getPreferredUserId() {
        return null;
    }

    @Override
    public void setPreferredUserId(KapuaId preferredUserId) {

    }

    @Override
    public KapuaId getScopeId() {
        return new KapuaEid(scopeId);
    }

}
