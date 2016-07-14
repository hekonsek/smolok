package smolok.bootstrap

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Smolok.class)
class SmolokTest {

    @Autowired
    ApplicationContext applicationContext

    @Test
    void shouldStartSpring() {
        assertThat(applicationContext).isNotNull()
    }

}