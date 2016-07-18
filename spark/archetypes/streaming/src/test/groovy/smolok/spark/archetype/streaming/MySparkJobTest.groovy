package smolok.spark.archetype.streaming

import com.google.common.io.Files
import org.junit.Test

class MySparkJobTest implements Serializable {

    @Test
    void shouldInjectSparkContext() {
        System.setProperty('spark.master', 'local[*]')
        System.setProperty('data', Files.createTempDir().absolutePath)
        System.setProperty('awaitTermination', "${false}")

        MySparkJob.main()
    }

}
