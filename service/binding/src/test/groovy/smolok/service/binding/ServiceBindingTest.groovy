/**
 * Licensed to the Rhiot under one or more
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
package smolok.service.binding

import org.junit.BeforeClass;
import org.junit.Test
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import smolok.bootstrap.Smolok
import smolok.eventbus.client.EventBus

import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort
import static smolok.eventbus.client.Header.arguments;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Smolok.class)
@Configuration
public class ServiceBindingTest {

    @Autowired
    EventBus eventBus

    @BeforeClass
    static void beforeClass() {
        System.setProperty('amqp.port', "${findAvailableTcpPort()}")
    }

    // Tests

    @Test
    public void shouldBindServiceToChannel() {
        def payload = 100L
        def response = eventBus.fromBus("echo.echo", payload, long.class);
        assertThat(response).isEqualTo(payload);
    }

    @Test
    public void shouldBindServiceToChannelUsingDestinationOnly() {
        def payload = 100L
        def response = eventBus.fromBus("echo.echo." + payload, long.class);
        assertThat(response).isEqualTo(payload);
    }

    @Test
    public void shouldHandlePostedMap() {
        int receivedSize = eventBus.fromBus("echo.sizeOfMap", ["foo": "foo", "bar": "bar"], int.class)
        assertThat(receivedSize).isEqualTo(2)
    }

    @Test
    public void shouldHandleArgumentAndPojo() {
        String stringPayload = "foo";
        def mapPayload = ["foo": "foo", "bar": "bar"]
        def received = eventBus.fromBus("echo.stringAndPojoToStringOperation.foo", mapPayload, String.class);
        assertThat(received).isEqualTo(stringPayload + mapPayload.size());
    }

    @Test
    public void shouldHandleHeaderArgumentAndPojo() {
        String stringPayload = "foo";
        def mapPayload = ["foo": "foo", "bar": "bar"]
        String received = eventBus.fromBus("echo.stringAndPojoToStringOperation", mapPayload, String.class, arguments("foo"));
        assertThat(received).isEqualTo(stringPayload + mapPayload.size());
    }

    @Test
    void shouldHandleHeaderArguments() {
        def received = eventBus.fromBus('echo.multiply', int.class, arguments(1, 2, 3))
        assertThat(received).isEqualTo(6)
    }

    @Test
    void shouldPreserveHeaderArgumentsOrder() {
        def received = eventBus.fromBus('echo.concatenate', String.class, arguments(1, 2, 3))
        assertThat(received).isEqualTo('123')
    }

    @Test
    void shouldBindTenantToArgument() {
        def received = eventBus.fromBus('echo.tenant', String.class)
        assertThat(received).isEqualTo('tenant')
    }

    @Test
    void shouldBindTenantBeforeBody() {
        def received = eventBus.fromBus('echo.tenantBeforePayload', 'foo', String.class)
        assertThat(received).isEqualTo('tenantfoo')
    }

    @Test
    void shouldBindTenantBeforeHeader() {
        def received = eventBus.fromBus('echo.tenantBeforePayload', String.class, arguments('foo'))
        assertThat(received).isEqualTo('tenantfoo')
    }

    @Test
    void shouldBindTenantBeforeChannelArgument() {
        def received = eventBus.fromBus('echo.tenantBeforePayload.foo', String.class)
        assertThat(received).isEqualTo('tenantfoo')
    }

    // Beans fixtures

    @Component
    public static class EchoServiceBinding extends ServiceBinding {

        @Autowired
        public EchoServiceBinding(AuthenticationProvider authenticationProvider) {
            super(authenticationProvider, "echo");
        }

    }

    public static interface EchoService {

        long echo(long value);

        long multiply(int a, int b, int c);

        String concatenate(int a, int b, int c);

        long sizeOfMap(Map map);

        String stringAndPojoToStringOperation(String string, Map<String, String> pojo)

        String tenant(@Tenant String tenant)

        String tenantBeforePayload(@Tenant String tenant, String payload)

    }

    @Component('echo')
    static class DefaultEchoService implements EchoService {

        @Override
        long echo(long value) {
            value
        }

        @Override
        long multiply(int a, int b, int c) {
            a * b * c
        }

        @Override
        String concatenate(int a, int b, int c) {
            "${a}${b}${c}"
        }

        @Override
        long sizeOfMap(Map map) {
            map.size()
        }

        @Override
        public String stringAndPojoToStringOperation(String string, Map<String, String> pojo) {
            return string + pojo.size();
        }

        @Override
        String tenant(@Tenant String tenant) {
            tenant
        }

        @Override
        String tenantBeforePayload(@Tenant String tenant, String payload) {
            tenant + payload
        }

    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        new MockAutenticationProvider('username', 'tenant')
    }

}