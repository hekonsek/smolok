package smolok.spark.archetype.rdd

import smolok.lib.spark.job.RddJobContext

class MySparkJob {

    static def job = new RddJobContext()

    static void main(String... args) {
        def rdd = job.source('text-file:/var/smolok/spark/data/foo.txt')
        def result = rdd.map{ String line -> line.toInteger() }.fold(0){ a, b ->
            a + b
        }

        println(result)
    }

}