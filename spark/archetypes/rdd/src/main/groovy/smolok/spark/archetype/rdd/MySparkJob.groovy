package smolok.spark.archetype.rdd

import org.apache.spark.api.java.function.Function2
import smolok.lib.spark.SparkJob

class MySparkJob {

    static void main(String... args) throws IOException, InterruptedException {
        SparkJob.onRequest { SparkJobRequest ->
            def rdd = SparkJob.rdd('parallelize:1,2,3')

            def result = rdd.fold(0, { Integer a, Integer b ->
                a + b
            } as Function2)

            System.out.println(result);
        }
    }

}