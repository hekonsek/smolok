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
package org.eclipse.kapua.service.device.registry.mongodb;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.KapuaException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static smolok.lib.common.Networks.findAvailableTcpPort;

public class MongoDbDeviceRegistryServiceTest {

    static int mongoPort = findAvailableTcpPort();

    DeviceRegistryService registryService = new MongoDbDeviceRegistryService(new MongoClient("localhost", mongoPort), "smolok", "devices");

    @BeforeClass
    public static void beforeClass() throws IOException {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(mongoPort, Network.localhostIsIPv6()))
                .build();

        starter.prepare(mongodConfig).start();
    }

    // Tests

    @Test
    public void shouldReturnDeviceWithScopedId() throws KapuaException {
        DeviceCreatorImpl deviceCreator = new DeviceCreatorImpl(BigInteger.ONE);
        Device device = registryService.create(deviceCreator);

        // Then
        assertThat(device.getScopeId().getId()).isEqualTo(deviceCreator.getScopeId().getId());
        assertThat(device.getId().getId()).isNotNull();
    }

    @Test
    public void shouldRegisterDevice() throws KapuaException {
        DeviceCreatorImpl deviceCreator = new DeviceCreatorImpl(BigInteger.TEN);
        Device device = registryService.create(deviceCreator);

        // When
        Device deviceFound = registryService.find(device.getScopeId(), device.getId());

        // Then
        assertThat(deviceFound).isNotNull();
    }

    @Test
    public void shouldFindByClientId() throws KapuaException {
        // Given
        DeviceCreatorImpl deviceCreator = new DeviceCreatorImpl(BigInteger.TEN);
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
        DeviceCreatorImpl deviceCreator = new DeviceCreatorImpl(BigInteger.TEN);
        Device device = registryService.create(deviceCreator);
        device.setClientId("clientId");
        registryService.update(device);

        // When
        Device deviceFound = registryService.find(device.getScopeId(), device.getId());

        // Then
        assertThat(deviceFound.getClientId()).isEqualTo(device.getClientId());
    }

}