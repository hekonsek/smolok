package smolok.lib.spark.streaming

import org.apache.spark.api.java.function.Function

import smolok.lib.spark.SparkJobRequest

import static smolok.lib.spark.SparkJobRequest.onRequest

class MySparkJob {

    static void main(String... args) {
        onRequest { SparkJobRequest request ->
            def path = System.getProperty('data', '/var/smolok/spark/data/streaming-demo')
            def stream = request.streamingContext.textFileStream(path)
            stream.filter(new Function<String, Boolean>() {
                @Override
                Boolean call(String line) throws Exception {
                    line.contains('stream')
                }
            }).count().print()
        }
    }

}