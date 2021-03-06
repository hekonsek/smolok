package net.smolok.service.binding

import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.test.context.junit4.SpringRunner

import smolok.eventbus.client.EventBus
import net.smolok.service.binding.security.AuthenticationProvider
import net.smolok.service.binding.security.CredentialsHolder
import net.smolok.service.binding.security.MockAutenticationProvider

import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.util.SocketUtils.findAvailableTcpPort
import static smolok.eventbus.client.Header.arguments
import static smolok.lib.common.Properties.setIntProperty

@RunWith(SpringRunner)
@SpringBootTest(classes = [KapuaApplication, ServiceBindingTest])
@Configuration
class ServiceBindingTest {

    // Event Bus fixtures

    @Autowired
    EventBus eventBus

    @BeforeClass
    static void beforeClass() {
       setIntProperty('EVENTBUS_SERVICE_PORT', findAvailableTcpPort())
    }

    // Tests

    @Test
    void shouldBindServiceToChannel() {
        def payload = 100L
        def response = eventBus.fromBus("echo.echo", payload, long.class)
        assertThat(response).isEqualTo(payload)
    }

    @Test
    void shouldBindServiceToChannelUsingDestinationOnly() {
        def payload = 100L
        def response = eventBus.fromBus("echo.echo." + payload, long.class);
        assertThat(response).isEqualTo(payload);
    }

    @Test
    void shouldHandlePostedMap() {
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

    @Test
    void shouldResolveBoundTenant() {
        def received = eventBus.fromBus('echo.boundTenant', String.class)
        assertThat(received).isEqualTo('tenant')
    }

    // Beans fixtures

    @Bean(initMethod = 'start')
    ServiceBinding echoServiceBinding(ServiceBindingFactory serviceBindingFactory) {
        serviceBindingFactory.serviceBinding('echo')
    }

    public static interface EchoService {

        long echo(long value);

        long multiply(int a, int b, int c);

        String concatenate(int a, int b, int c);

        long sizeOfMap(Map map);

        String stringAndPojoToStringOperation(String string, Map<String, String> pojo)

        String tenant(@Tenant String tenant)

        String tenantBeforePayload(@Tenant String tenant, String payload)

        String boundTenant()

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

        @Override
        String boundTenant() {
            CredentialsHolder.credentials().tenant()
        }

    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        new MockAutenticationProvider('username', 'tenant')
    }

}