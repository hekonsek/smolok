package org.eclipse.kapua.service.device.registry.mongodb;


import com.mongodb.Mongo;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
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

public class MongoDbDeviceRegistryServiceTest {

    DeviceRegistryService registryService = new MongoDbDeviceRegistryService(new Mongo(), "xxx", "yyy");

    @BeforeClass
    public static void beforeClass() throws IOException {
        MongodStarter starter = MongodStarter.getDefaultInstance();

        int port = 27017;
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();

        MongodExecutable mongodExecutable = null;
            mongodExecutable = starter.prepare(mongodConfig);
            MongodProcess mongod = mongodExecutable.start();
    }

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

}