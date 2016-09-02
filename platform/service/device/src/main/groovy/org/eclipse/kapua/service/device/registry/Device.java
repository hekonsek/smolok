/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry;

import java.util.Date;

/**
 * Device is an object representing a device or gateway connected to the Kapua platform.
 * The Device object contains several attributes regarding the Device itself and its software configuration.
 */
public interface Device extends KapuaUpdatableEntity
{
    public static final String TYPE = "dvce";

    default public String getType()
    {
        return TYPE;
    }

    public String getClientId();

    public void setClientId(String clientId);

    public DeviceStatus getStatus();

    public void setStatus(DeviceStatus status);

    public String getDisplayName();

    public void setDisplayName(String diplayName);

    public Date getLastEventOn();

    public void setLastEventOn(Date lastEventOn);

    public DeviceEventType getLastEventType();

    public void setLastEventType(DeviceEventType lastEventType);

    public String getSerialNumber();

    public void setSerialNumber(String serialNumber);

    public String getModelId();

    public void setModelId(String modelId);

    public String getImei();

    public void setImei(String imei);

    public String getImsi();

    public void setImsi(String imsi);

    public String getIccid();

    public void setIccid(String iccid);

    public String getBiosVersion();

    public void setBiosVersion(String biosVersion);

    public String getFirmwareVersion();

    public void setFirmwareVersion(String firmwareVersion);

    public String getOsVersion();

    public void setOsVersion(String osVersion);

    public String getJvmVersion();

    public void setJvmVersion(String jvmVersion);

    public String getOsgiFrameworkVersion();

    public void setOsgiFrameworkVersion(String osgiFrameworkVersion);

    public String getApplicationFrameworkVersion();

    public void setApplicationFrameworkVersion(String appFrameworkVersion);

    public String getApplicationIdentifiers();

    public void setApplicationIdentifiers(String applicationIdentifiers);

    public String getAcceptEncoding();

    public void setAcceptEncoding(String acceptEncoding);

    public Double getGpsLongitude();

    public void setGpsLongitude(Double gpsLongitude);

    public Double getGpsLatitude();

    public void setGpsLatitude(Double gpsLatitude);

    public String getCustomAttribute1();

    public void setCustomAttribute1(String customAttribute1);

    public String getCustomAttribute2();

    public void setCustomAttribute2(String customAttribute2);

    public String getCustomAttribute3();

    public void setCustomAttribute3(String customAttribute3);

    public String getCustomAttribute4();

    public void setCustomAttribute4(String customAttribute4);

    public String getCustomAttribute5();

    public void setCustomAttribute5(String customAttribute5);

    public DeviceCredentialsMode getCredentialsMode();

    public void setCredentialsMode(DeviceCredentialsMode credentialsMode);

    public KapuaId getPreferredUserId();

    public void setPreferredUserId(KapuaId deviceUserId);

    /*
     * // --------------------------
     * //
     * // non-indexed properties
     * //
     * // --------------------------
     * public long getUptime();
     * 
     * public void setUptime(long uptime);
     * 
     * public String getModelName();
     * 
     * public void setModelName(String modelName);
     * 
     * public String getPartNumber();
     * 
     * public void setPartNumber(String partNumber);
     * 
     * public String getAvailableProcessors();
     * 
     * public void setAvailableProcessors(String availableProcessors);
     * 
     * public String getTotalMemory();
     * 
     * public void setTotalMemory(String totalMemory);
     * 
     * public String getOs();
     * 
     * public void setOs(String os);
     * 
     * public String getOsArch();
     * 
     * public void setOsArch(String osArch);
     * 
     * public String getJvmName();
     * 
     * public void setJvmName(String jvmName);
     * 
     * public String getJvmProfile();
     * 
     * public void setJvmProfile(String jvmProfile);
     * 
     * public String getOsgiFramework();
     * 
     * public void setOsgiFramework(String osgiFramework);
     * 
     * public String getConnectionInterface();
     * 
     * public void setConnectionInterface(String connectionInterface);
     * 
     * public Double getGpsAltitude();
     * 
     * public void setGpsAltitude(Double gpsAltitude);
     */
}
