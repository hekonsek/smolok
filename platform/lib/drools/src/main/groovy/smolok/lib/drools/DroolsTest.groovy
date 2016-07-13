package smolok.lib.drools

import org.springframework.boot.builder.SpringApplicationBuilder
import smolok.lib.drools.spring.DroolsConfiguration

class DroolsTest {

    public static void main(String[] args) {
        def drools = new SpringApplicationBuilder(DroolsConfiguration.class).run(args).getBean(Drools.class)
        def containerId = System.currentTimeMillis() + ''
        drools.createContainer(containerId, 'smolok', 'smolok-lib-drools-sample', '0.0.0-SNAPSHOT')
        drools.insert(containerId, "foo")
    }

}
