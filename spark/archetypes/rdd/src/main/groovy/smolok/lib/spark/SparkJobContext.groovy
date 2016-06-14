package smolok.lib.spark

import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationContext

class SparkJobContext {

    static ApplicationContext applicationContext

    static {
        applicationContext = new SpringApplicationBuilder(SparkConfiguration.class).build().run()
    }

    static ApplicationContext applicationContext() {
        applicationContext
    }

    static JavaRDD rdd(String uri) {
        if(uri.startsWith('parallelize')) {
            def collection = uri.substring('parallelize'.length() + 1)
            def sc = applicationContext.getBean(JavaSparkContext.class)
            def shell = new GroovyShell(SparkJobContext.class.classLoader)
            shell.setVariable('sc', sc)
            shell.evaluate("sc.parallelize([${collection}])")
        }
    }

}
