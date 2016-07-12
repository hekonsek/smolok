package smolok.lib.drools

import org.springframework.boot.builder.SpringApplicationBuilder
import smolok.lib.drools.spring.DroolsConfiguration

class DroolsTest {

    public static void main(String[] args) {
        def drools = new SpringApplicationBuilder(DroolsConfiguration.class).run(args).getBean(Drools.class)
        drools.createContainer('hello', 'foo', 'bar', '1.0')
        drools.insert('hello', "foo")
    }

}
