package smolok.lib.spark.job

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.Validate
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext

import static java.io.File.createTempFile
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic

class RddJobContext {

    public static final String OPTION_TESTING = 'testing'

    private final String[] arguments

    private final JavaSparkContext sparkContext

    RddJobContext(String... arguments) {
        this.arguments = arguments

        def master = option('spark.master', testing ? 'local[*]' : 'spark://localhost:7077')
        def jobName = option('job.name', "Spark job ${randomAlphabetic(5)}")

        def sparkConfig = new SparkConf().setMaster(master).setAppName(jobName)
        if(testing) {
            sparkConfig.set('spark.driver.allowMultipleContexts', "${true}")
        }

        sparkContext = new JavaSparkContext(sparkConfig)
    }

    // Sources

    JavaRDD source(String uri) {
        if(uri.startsWith('parallelize')) {
            def collection = uri.substring('parallelize'.length() + 1)
            def shell = new GroovyShell(RddJobContext.class.classLoader)
            shell.setVariable('sc', sparkContext)
            shell.evaluate("sc.parallelize([${collection}])")
        } else if(uri.startsWith('text-file')) {
            def path = uri.substring('text-file'.length() + 1)
            if(testing) {
                def testFile = createTempFile('smolok', 'test-file')
                def data = getClass().getResourceAsStream(path)
                Validate.notNull(data, 'No data file found in a classpath.')
                def out = new FileOutputStream(testFile)
                IOUtils.copy(data, out)
                out.close()
                sparkContext.textFile(testFile.absolutePath)
            } else {
                sparkContext.textFile(path)
            }
        }
    }

    // Options operations

    Optional<String> option(String name) {
        def fromSystem = System.getProperty(name)
        if(fromSystem != null) {
            return Optional.of(fromSystem)
        }
        Optional.ofNullable(arguments.find{ name.startsWith(/--${name}=/) })
    }

    String option(String name, String defaultValue) {
        option(name).orElse(defaultValue)
    }

    Optional<Boolean> booleanOption(String name) {
        def stringOption = option(name)
        if(stringOption.present) {
            Optional.of(stringOption.get().toBoolean())
        } else {
            Optional.empty()
        }
    }

    boolean booleanOption(String name, boolean defaultValue) {
        booleanOption(name).orElse(defaultValue)
    }

    // Accessors

    JavaSparkContext sparkContext() {
        sparkContext
    }

    // Test helpers

    static void enableTesting() {
        System.setProperty(OPTION_TESTING, "${true}")
    }

    boolean isTesting() {
        booleanOption(OPTION_TESTING, false)
    }

}
