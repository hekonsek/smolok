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

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.process.runtime.Network;
import net.smolok.service.documentstore.mongodb.MongodbDocumentStore;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.KapuaException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import static de.flapdoodle.embed.mongo.distribution.Version.Main.PRODUCTION;
import static org.assertj.core.api.Assertions.assertThat;
import static smolok.lib.common.Networks.findAvailableTcpPort;

public class MongoDbDeviceRegistryServiceTest {

    // MongoDB fixtures

    static int mongoPort = findAvailableTcpPort();

    MongoClient mongoClient = new MongoClient("localhost", mongoPort);

    DeviceRegistryService registryService = new MongoDbDeviceRegistryService(
            new MongodbDocumentStore(mongoClient, "devices"), mongoClient, "devices", "devices"
    );

    @BeforeClass
    public static void beforeClass() throws IOException {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(PRODUCTION)
                .net(new Net(mongoPort, Network.localhostIsIPv6()))
                .build();

        MongodStarter.getDefaultInstance().prepare(mongodConfig).start();
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
        registryService.delete(device);

        // Then
        assertThat(registryService.find(device.getScopeId(), device.getId())).isNull();
    }

}