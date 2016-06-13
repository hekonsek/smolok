package smolok.spark.archetype.rdd

import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.api.java.function.Function2
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

import static org.slf4j.LoggerFactory.getLogger

@SpringBootApplication
class SparkJob {

    private static final LOG = getLogger(SparkJob.class)

    public static void main(String... args) throws IOException, InterruptedException {
        def app = new SpringApplicationBuilder(SparkJob.class).build().run(args)

        def rdd = app.getBean(JavaSparkContext.class).parallelize([1, 2, 3])

        def result = rdd.fold(0, { Integer a, Integer b ->
            a + b
        } as Function2)

        app.close()
        System.out.println(result);
    }

}