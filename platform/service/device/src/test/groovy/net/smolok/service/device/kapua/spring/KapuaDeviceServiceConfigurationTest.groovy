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
package net.smolok.service.device.kapua.spring

import net.smolok.service.device.api.Device
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import smolok.eventbus.client.EventBus

import static net.smolok.service.device.api.Device.device
import static net.smolok.service.documentstore.api.QueryBuilder.queryBuilder
import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort
import static smolok.lib.common.Properties.setIntProperty
import static smolok.lib.common.Uuids.uuid

@RunWith(SpringRunner)
@SpringBootTest(classes = KapuaApplication)
class KapuaDeviceServiceConfigurationTest {

    @Autowired
    EventBus eventBus

    @BeforeClass
    static void beforeClass() {
        setIntProperty('spring.data.mongodb.port', findAvailableTcpPort())
    }

    def deviceId = uuid()

    @Before
    void before() {
        eventBus.toBusAndWait('device.register', device(deviceId))
    }

    // Tests

    @Test
    void shouldRegisterAndGetDevice() {
        // When
        def device = eventBus.fromBus('device.get', deviceId, Device.class)

        // Then
        assertThat(device.deviceId).isEqualTo(deviceId)
    }

    @Test
    void doubleRegistrationShouldUpdate() {
        // Given
        def countBeforeSecondRegistration = eventBus.fromBus('device.count', queryBuilder(), long.class)

        // When
        eventBus.toBusAndWait('device.register', device('myDevice'))
        def countAfterSecondRegistration = eventBus.fromBus('device.count', queryBuilder(), long.class)

        // Then
        assertThat(countBeforeSecondRegistration).isEqualTo(countAfterSecondRegistration)
    }

    @Test
    void shouldGenerateId() {
        // When
        eventBus.toBusAndWait('device.register', device('myDevice'))

        // Then
        def device = eventBus.fromBus('device.get', 'myDevice', Device.class)
        assertThat(device.properties.kapuaId).isNotNull()
    }

    @Test
    void shouldDeregisterDevice() {
        // When
        eventBus.toBusAndWait('device.deregister', deviceId)

        // Then
        def device = eventBus.fromBus('device.get', deviceId, Device.class)
        assertThat(device).isNull()
    }

    @Test
    void shouldNotGetDevice() {
        def device = eventBus.fromBus('device.get', 'someRandomDevice', Device.class)
        assertThat(device).isNull();
    }

    @Test
    void shouldCountDevices() {
        // Given
        eventBus.toBusAndWait('device.register', device('countDevice'))

        // When
        def devices = eventBus.fromBus('device.count', queryBuilder([deviceId: 'countDevice']), long.class)

        // Then
        assertThat(devices).isEqualTo(1L)
    }

    @Test
    void shouldFindDevices() {
        // Given
        eventBus.toBusAndWait('device.register', device(deviceId))

        // When
        Device[] devices = eventBus.fromBus('device.find', queryBuilder([deviceId: deviceId]), Device[].class)

        // Then
        assertThat(devices.toList()).hasSize(1)
    }

    @Test
    void foundDevicedShouldRememberKapuaId() {
        // Given
        eventBus.toBusAndWait('device.register', device(deviceId))

        // When
        Device[] devices = eventBus.fromBus('device.find', queryBuilder([deviceId: deviceId]), Device[].class)

        // Then
        assertThat(devices[0].properties.kapuaId).isNotNull()
    }

    @Test
    void foundDevicedShouldRememberKapuaScopeId() {
        // Given
        eventBus.toBusAndWait('device.register', device(deviceId))

        // When
        Device[] devices = eventBus.fromBus('device.find', queryBuilder([deviceId: deviceId]), Device[].class)

        // Then
        assertThat(devices[0].properties.kapuaScopeId).isNotNull()
    }

    @Test
    void shouldLoadRegistrationDateFromKapua() {
        // Given
        eventBus.toBusAndWait('device.register', device('myDevice'))

        // When
        def device = eventBus.fromBus('device.get', 'myDevice', Device.class)

        // Then
        assertThat(device.registrationDate).isNotNull()
    }

    @Test
    void shouldUpdateDevice() {
        // Given
        eventBus.toBusAndWait('device.register', device('myDevice'))
        def device = eventBus.fromBus('device.get', 'myDevice', Device.class)
        device.properties.foo = 'bar'

        // When
        eventBus.toBusAndWait('device.update', device)

        // Then
        def updatedDevice = eventBus.fromBus('device.get', 'myDevice', Device.class)
        assertThat(updatedDevice.properties.foo).isEqualTo('bar')
    }

}