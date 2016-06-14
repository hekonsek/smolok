package smolok.lib.spark

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationContext

class JobContext {

    static ApplicationContext applicationContext

    static {
        applicationContext = new SpringApplicationBuilder(SparkConfiguration.class).build().run()
    }

    static ApplicationContext applicationContext() {
        applicationContext
    }

}
