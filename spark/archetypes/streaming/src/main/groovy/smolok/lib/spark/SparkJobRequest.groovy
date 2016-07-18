package smolok.lib.spark

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext

import java.util.concurrent.TimeUnit

class SparkJobRequest {

    JavaStreamingContext streamingContext

    SparkJobRequest() {
        def master = System.getProperty('spark.master', 'spark://localhost:7077')

        def sparkConfig = new SparkConf().setMaster(master).setAppName('MySparkJob')
        streamingContext = new JavaStreamingContext(sparkConfig, Durations.seconds(5))
    }

    static void onRequest(Closure closure) {
        def request = new SparkJobRequest()
        closure(request)
        request.streamingContext.start()
        if(System.getProperty('awaitTermination', 'true').toBoolean()) {
            request.streamingContext.awaitTerminationOrTimeout(TimeUnit.MINUTES.toMillis(5))
        }
    }

}
