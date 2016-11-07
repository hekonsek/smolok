package smolok.bootstrap

import org.apache.camel.component.amqp.AMQPComponent
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringRunner)
@SpringBootTest(classes = KapuaApplication)
class SmolokTest {

    @Autowired
    AMQPComponent amqp

    @Test
    void shouldLoadAmqpComponent() {
        assertThat(amqp).isNotNull()
    }

}