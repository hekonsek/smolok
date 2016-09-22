/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.kapua.service.device.registry.mongodb;

import net.smolok.service.documentstore.mongodb.spring.MongodbDocumentStoreConfiguration;
import org.eclipse.kapua.service.device.registry.*;
import org.eclipse.kapua.service.device.registry.mongodb.spring.MongodbDeviceRegistryServiceConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static smolok.lib.common.Networks.findAvailableTcpPort;
import static smolok.lib.common.Properties.setIntProperty;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MongoAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class,
        MongodbDocumentStoreConfiguration.class, MongodbDeviceRegistryServiceConfiguration.class})
public class MongoDbDeviceRegistryServiceTest {

    @Autowired
    DeviceRegistryService registryService;

    // MongoDB fixtures

    @BeforeClass
    public static void beforeClass() {
        setIntProperty("spring.data.mongodb.port", findAvailableTcpPort());
    }

    // Device fixtures

    static Random rnd = new Random();

    private BigInteger scopedId = BigInteger.valueOf(rnd.nextLong());

    // Tests

    @Test
    public void shouldReturnDeviceWithScopedId() throws KapuaException {
        // Given
        SimpleDeviceCreator deviceCreator = new SimpleDeviceCreator(scopedId);

        // When
        Device device = registryService.create(deviceCreator);

        // Then
        assertThat(device.getScopeId().getId()).isEqualTo(deviceCreator.getScopeId().getId());
        assertThat(device.getId().getId()).isNotNull();
    }

    @Test
    public void shouldRegisterDevice() throws KapuaException {
        SimpleDeviceCreator deviceCreator = new SimpleDeviceCreator(BigInteger.TEN);
        Device device = registryService.create(deviceCreator);

        // When
        Device deviceFound = registryService.find(device.getScopeId(), device.getId());

        // Then
        assertThat(deviceFound).isNotNull();
    }

    @Test
    public void shouldFindByClientId() throws KapuaException {
        // Given
        SimpleDeviceCreator deviceCreator = new SimpleDeviceCreator(BigInteger.TEN);
        deviceCreator.setClientId("clientId");
        Device device = registryService.create(deviceCreator);

        // When
        Device deviceFound = registryService.findByClientId(device.getScopeId(), deviceCreator.getClientId());

        // Then
        assertThat(deviceFound).isNotNull();
    }

    @Test
    public void shouldUpdateDevice() throws KapuaException {
        // Given
        SimpleDeviceCreator deviceCreator = new SimpleDeviceCreator(BigInteger.TEN);
        Device device = registryService.create(deviceCreator);
        device.setClientId("clientId");
        registryService.update(device);

        // When
        Device deviceFound = registryService.find(device.getScopeId(), device.getId());

        // Then
        assertThat(deviceFound.getClientId()).isEqualTo(device.getClientId());
    }

    @Test
    public void shouldDeleteDevice() throws KapuaException {
        // Given
        SimpleDeviceCreator deviceCreator = new SimpleDeviceCreator(BigInteger.TEN);
        Device device = registryService.create(deviceCreator);

        // When
        registryService.delete(device.getScopeId(), device.getId());

        // Then
        assertThat(registryService.find(device.getScopeId(), device.getId())).isNull();
    }

    @Test
    public void shouldCountDevice() throws KapuaException {
        // Given
        SimpleDeviceCreator deviceCreator = new SimpleDeviceCreator(BigInteger.TEN);
        deviceCreator.setClientId("foo");
        registryService.create(deviceCreator);
        DeviceQuery query = new DeviceQueryImpl(new KapuaEid(BigInteger.TEN));
        query.setPredicate(new AttributePredicate<>("clientId", "foo"));

        // When
        long devices = registryService.count(query);

        // Then
        assertThat(devices).isEqualTo(1);
    }

}