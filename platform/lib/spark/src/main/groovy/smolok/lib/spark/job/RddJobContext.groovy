package smolok.lib.spark.job

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.Validate
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext

import static java.io.File.createTempFile
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import static smolok.lib.common.Properties.stringProperty

/**
 * Context class around RDD job execution providing helper methods to simplify working with RDD data sets.
 */
class RddJobContext {

    public static final String OPTION_TESTING = 'testing'

    // Members

    private final String[] arguments

    // Member collaborators

    private final JavaSparkContext sparkContext

    // Constructors

    RddJobContext(JavaSparkContext sparkContext, String... arguments) {
        this.arguments = arguments
        this.sparkContext = sparkContext
    }

    RddJobContext(SparkContext sparkContext, String... arguments) {
        this(new JavaSparkContext(sparkContext), arguments)
    }

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

    // Data sources

    JavaRDD source(String uri) {
        if(uri.startsWith('list')) {
            def collection = uri.substring('list'.length() + 1)
            def shell = new GroovyShell(RddJobContext.class.classLoader)
            shell.setVariable('sc', sparkContext)
            shell.evaluate("sc.parallelize([${collection}])")
        } else if(uri.startsWith('text-file')) {
            def path = uri.substring('text-file'.length() + 1)
            if(testing) {
                def testFile = createTempFile('smolok', 'test-file')
                def data = getClass().getResourceAsStream(path)
                Validate.notNull(data, "No data file found in a classpath: ${path}")
                def out = new FileOutputStream(testFile)
                IOUtils.copy(data, out)
                out.close()
                sparkContext.textFile(testFile.absolutePath)
            } else {
                sparkContext.textFile(path)
            }
        } else {
            def sourceFromOption = option(uri)
            sourceFromOption.isPresent() ? source(sourceFromOption.get()) : null
        }
    }

    // Options operations

    Optional<String> option(String name) {
        def fromResolver = stringProperty(name)
        if(fromResolver != null) {
            return Optional.of(fromResolver)
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
