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
package net.smolok.service.device.api
/**
 * Represents information about device registered in a cloud service.
 */
class Device {

    private String deviceId

    private Date registrationDate

    private Date lastUpdate

    private def properties = new HashMap<String, Object>()

    // Constructors

    public Device() {
    }

    public Device(String deviceId, Date registrationDate, Date lastUpdate, Map<String, Object> properties) {
        this.deviceId = deviceId;
        this.registrationDate = registrationDate;
        this.lastUpdate = lastUpdate;
        this.properties = setProperties(properties)
    }

    public static Device minimalDevice(String deviceId) {
        Device device = new Device();
        device.setDeviceId(deviceId);
        return device;
    }

    // Getters and setters


    String getDeviceId() {
        deviceId
    }

    void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    Date getRegistrationDate() {
        registrationDate;
    }

    void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    Date getLastUpdate() {
        return lastUpdate;
    }

    void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    Map<String, Object> getProperties() {
        properties
    }

    void setProperties(Map<String, Object> properties) {
        this.properties = new HashMap<>(properties)
    }

}