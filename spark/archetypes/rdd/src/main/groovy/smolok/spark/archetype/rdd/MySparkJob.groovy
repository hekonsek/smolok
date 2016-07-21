package smolok.spark.archetype.rdd

import org.apache.spark.api.java.function.Function2

import smolok.lib.spark.job.RddJobContext

class MySparkJob implements Serializable {

    static def job = new RddJobContext()

    static void main(String... args) {
        def rdd = job.source('parallelize:1,2,3')
        def result = rdd.fold(0, { Integer a, Integer b ->
            a + b
        } as Function2)

        System.out.println(result);
    }

}