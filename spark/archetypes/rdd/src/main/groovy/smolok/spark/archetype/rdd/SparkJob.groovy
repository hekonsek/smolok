package smolok.spark.archetype.rdd

import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.api.java.function.Function2
import smolok.lib.spark.JobContext

class SparkJob {

    static void main(String... args) throws IOException, InterruptedException {
        def rdd = JobContext.applicationContext().getBean(JavaSparkContext.class).parallelize([1, 2, 3])

        def result = rdd.fold(0, { Integer a, Integer b ->
            a + b
        } as Function2)

        System.out.println(result);
    }

}