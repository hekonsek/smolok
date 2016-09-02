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

/**
 * DeviceCreator encapsulates all the information needed to create a new Device in the system.
 * The data provided will be used to seed the new Device and its related information.
 * The fields of the DeviceCreator presents the attributes that are searchable for a given device.
 * The DeviceCreator Properties field can be used to provide additional properties associated to the Device;
 * those properties will not be searchable through Device queries.
 * The clientId field of the Device is used to store the MAC address of the primary network interface of the device.
 */
public interface DeviceCreator extends KapuaUpdatableEntityCreator<Device>
{
    public String getClientId();

    public void setClientId(String clientId);

    public String getDisplayName();

    public void setDisplayName(String displayName);

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

    public void setPreferredUserId(KapuaId preferredUserId);
}
