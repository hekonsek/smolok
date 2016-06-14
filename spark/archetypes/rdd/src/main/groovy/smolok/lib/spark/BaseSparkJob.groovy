package smolok.lib.spark

import static org.slf4j.LoggerFactory.getLogger

abstract class BaseSparkJob implements Serializable {

    private final LOG = getLogger(getClass())

    abstract void execute(String... args)

}
