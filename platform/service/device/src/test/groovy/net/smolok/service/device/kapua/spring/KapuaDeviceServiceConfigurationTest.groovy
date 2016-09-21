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
import net.smolok.service.device.api.QueryBuilder
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import smolok.bootstrap.Smolok
import smolok.eventbus.client.EventBus

import static java.lang.System.setProperty
import static net.smolok.service.device.api.Device.minimalDevice
import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort

@RunWith(SpringRunner)
@SpringBootTest(classes = Smolok.class)
class KapuaDeviceServiceConfigurationTest {

    @Autowired
    EventBus eventBus

    @BeforeClass
    static void beforeClass() {
        setProperty("spring.data.mongodb", findAvailableTcpPort() + "");
    }

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
        def countBeforeSecondRegistration = eventBus.fromBus('device.count', QueryBuilder.queryBuilder([:]), long.class)

        // When
        eventBus.toBusAndWait('device.register', minimalDevice('myDevice'))
        def countAfterSecondRegistration = eventBus.fromBus('device.count', QueryBuilder.queryBuilder([:]), long.class)

        // Then
        assertThat(countBeforeSecondRegistration).isEqualTo(countAfterSecondRegistration)
    }

    @Test
    public void shouldGenerateId() {
        // When
        eventBus.toBusAndWait('device.register', minimalDevice('myDevice'))

        // Then
        def device = eventBus.fromBus('device.get', 'myDevice', Device.class)
        assertThat(device.properties.kapuaId).isNotNull()
    }

    @Test
    public void shouldDeregisterDevice() {
        // Given
        eventBus.toBusAndWait('device.register', minimalDevice('myDevice'))

        // When
        eventBus.toBusAndWait('device.deregister', 'myDevice')

        // Then
        def device = eventBus.fromBus('device.get', 'myDevice', Device.class)
        assertThat(device).isNull()
    }

//    @Test
//    public void shouldGetDevice() {
//        connector.toBusAndWait(registerDevice(), device);
//        Device loadedDevice = connector.fromBus(getDevice(device.getDeviceId()), Device.class);
//        Truth.assertThat(loadedDevice.getDeviceId()).isEqualTo(device.getDeviceId());
//    }
//
//    @Test
//    public void shouldNotGetDevice() {
//        Device loadedDevice = connector.fromBus(getDevice(device.getDeviceId()), Device.class);
//        Truth.assertThat(loadedDevice).isNull();
//    }
//
//    @Test
//    public void shouldSendHeartbeatDisconnected() {
//        // Given
//        device.setLastUpdate(new DateTime(device.getLastUpdate()).minusMinutes(2).toDate());
//        connector.toBusAndWait(registerDevice(), device);
//
//        // When
//        connector.toBusAndWait(deviceHeartbeat(device.getDeviceId()));
//
//        // Then
//        List<String> disconnected = connector.fromBus(disconnected(), List.class);
//        Truth.assertThat(disconnected).doesNotContain(device.getDeviceId());
//    }
//
//    // Device metrics tests
//
//    @Test
//    public void shouldReadEmptyMetric() {
//        String metric = connector.fromBus(readDeviceMetric(device.getDeviceId(), randomAlphabetic(10)), String.class);
//
//        // Then
//        Truth.assertThat(metric).isNull();
//    }
//
//    @Test
//    public void shouldReadStringMetric() {
//        // Given
//        connector.toBusAndWait(registerDevice(), device);
//        String metric = randomAlphabetic(10);
//        String value = randomAlphabetic(10);
//        connector.toBusAndWait(writeDeviceMetric(device.getDeviceId(), metric), value);
//
//        // When
//        String metricRead = connector.fromBus(readDeviceMetric(device.getDeviceId(), metric), String.class);
//
//        // Then
//        Truth.assertThat(metricRead).isEqualTo(value);
//    }
//
//    @Test
//    public void shouldReadIntegerMetric() {
//        // Given
//        connector.toBusAndWait(registerDevice(), device);
//        String metric = randomAlphabetic(10);
//        int value = 666;
//        connector.toBusAndWait(writeDeviceMetric(device.getDeviceId(), metric), value);
//
//        // When
//        int metricRead = connector.fromBus(readDeviceMetric(device.getDeviceId(), metric), int.class);
//
//        // Then
//        Truth.assertThat(metricRead).isEqualTo(value);
//    }
//
//    @Test
//    public void shouldPollStringMetric() {
//        // Given
//        device.setAddress("non empty");
//        connector.toBusAndWait(registerDevice(), device);
//        String metric = randomAlphabetic(10);
//
//        // When
//        String metricRead = connector.fromBus(readDeviceMetric(device.getDeviceId(), metric), String.class);
//
//        // Then
//        Truth.assertThat(metricRead).isEqualTo(metric);
//    }

}
