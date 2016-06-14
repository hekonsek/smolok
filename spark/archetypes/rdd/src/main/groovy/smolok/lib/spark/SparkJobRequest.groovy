package smolok.lib.spark

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext

class SparkJobRequest {

    JavaSparkContext sparkContext

    SparkJobRequest() {
        def master = System.getProperty('spark.master', 'spark://localhost:7077')

        def sparkConfig = new SparkConf().setMaster(master).setAppName('MySparkJob')
        sparkContext = new JavaSparkContext(sparkConfig)
    }

    JavaRDD rdd(String uri) {
        if(uri.startsWith('parallelize')) {
            def collection = uri.substring('parallelize'.length() + 1)
            def shell = new GroovyShell(SparkJobRequest.class.classLoader)
            shell.setVariable('sc', sparkContext)
            shell.evaluate("sc.parallelize([${collection}])")
        }
    }

    static void onRequest(Closure closure) {
        closure(new SparkJobRequest())
    }

}
