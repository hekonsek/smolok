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
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import smolok.bootstrap.Smolok
import smolok.eventbus.client.EventBus

import static net.smolok.service.device.api.Device.minimalDevice
import static net.smolok.service.documentstore.api.QueryBuilder.queryBuilder
import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort
import static smolok.lib.common.Properties.setIntProperty

@RunWith(SpringRunner)
@SpringBootTest(classes = Smolok.class)
class KapuaDeviceServiceConfigurationTest {

    @Autowired
    EventBus eventBus

    @BeforeClass
    static void beforeClass() {
        setIntProperty('spring.data.mongodb', findAvailableTcpPort())
    }

    // Tests

    @Test
    void shouldRegisterAndGetDevice() {
        // Given
        eventBus.toBusAndWait('device.register', minimalDevice('myDevice'))

        // When
        def device = eventBus.fromBus('device.get', 'myDevice', Device.class)

        // Then
        assertThat(device.deviceId).isEqualTo('myDevice')
    }

    @Test
    void doubleRegistrationShouldUpdate() {
        // Given
        eventBus.toBusAndWait('device.register', minimalDevice('myDevice'))
        def countBeforeSecondRegistration = eventBus.fromBus('device.count', queryBuilder(), long.class)

        // When
        eventBus.toBusAndWait('device.register', minimalDevice('myDevice'))
        def countAfterSecondRegistration = eventBus.fromBus('device.count', queryBuilder(), long.class)

        // Then
        assertThat(countBeforeSecondRegistration).isEqualTo(countAfterSecondRegistration)
    }

    @Test
    void shouldGenerateId() {
        // When
        eventBus.toBusAndWait('device.register', minimalDevice('myDevice'))

        // Then
        def device = eventBus.fromBus('device.get', 'myDevice', Device.class)
        assertThat(device.properties.kapuaId).isNotNull()
    }

    @Test
    void shouldDeregisterDevice() {
        // Given
        eventBus.toBusAndWait('device.register', minimalDevice('myDevice'))

        // When
        eventBus.toBusAndWait('device.deregister', 'myDevice')

        // Then
        def device = eventBus.fromBus('device.get', 'myDevice', Device.class)
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
        eventBus.toBusAndWait('device.register', minimalDevice('countDevice'))

        // When
        def devices = eventBus.fromBus('device.count', queryBuilder([deviceId: 'countDevice']), long.class)

        // Then
        assertThat(devices).isEqualTo(1L)
    }

}
