package net.smolok.service.binding.client.spring

import net.smolok.service.binding.ServiceBinding
import net.smolok.service.binding.ServiceBindingFactory
import net.smolok.service.binding.client.BindingClientFactory
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner

import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringRunner)
@SpringBootTest(classes = [KapuaApplication, BindingClientFactoryConfigurationTest])
class BindingClientFactoryConfigurationTest {

    @Bean
    TestService testService() {
        new TestServiceImpl()
    }

    @Bean
    ServiceBinding serviceBinding(ServiceBindingFactory serviceBindingFactory) {
        serviceBindingFactory.serviceBinding('testService')
    }

    @Bean
    TestService testServiceClient(BindingClientFactory bindingClientFactory) {
        bindingClientFactory.build(TestService.class, 'testService')
    }

    @Autowired
    TestService testServiceClient

    @Test
    void shouldAcceptSingleArgument() {
        def response = testServiceClient.echo('foo')
        assertThat(response).isEqualTo('foo')
    }

    @Test
    void shouldAcceptTwoArguments() {
        def response = testServiceClient.doubleEcho('foo', 'bar')
        assertThat(response).isEqualTo('foobar')
    }

    @Test
    void shouldAcceptNoArguments() {
        def response = testServiceClient.noArguments()
        assertThat(response).isEqualTo('echo')
    }

    static interface TestService {

        String echo(String echo)

        String doubleEcho(String firstEcho, String secondEcho)

        String noArguments()

    }

    static class TestServiceImpl implements TestService {

        @Override
        String echo(String echo) {
            echo
        }

        @Override
        String doubleEcho(String firstEcho, String secondEcho) {
            firstEcho + secondEcho
        }

        @Override
        String noArguments() {
            'echo'
        }

    }

}