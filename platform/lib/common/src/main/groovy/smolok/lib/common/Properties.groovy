package smolok.lib.common

import groovy.transform.CompileStatic

import java.lang.ThreadLocal as JThreadLocal
import java.util.Properties as JProperties

import static java.lang.System.getenv

@CompileStatic
final class Properties {

    // Members

    private static final JProperties applicationPropertiesFile = new JProperties()
    static {
        def propertiesStream = Properties.class.getResourceAsStream('/application.properties')
        if(propertiesStream != null) {
            applicationPropertiesFile.load(propertiesStream)
        }
    }

	private static final JThreadLocal<JProperties> threadLocalProperties = new ThreadLocal<JProperties>() {
        @Override
        protected JProperties initialValue() {
            new JProperties()
        }
    }
	
    private static JProperties propertiesSnapshot
    static {
        saveSystemProperties()
    }

    // Constructors

    private Properties() {
    }

    static boolean hasProperty(String key) {
        stringProperty(key) != null
    }

    // String properties

    static String stringProperty(String key, String defaultValue) {
		// lookup the thread local
		def property = threadLocalProperties.get().getProperty(key)
		if (property != null) {
			return property
		}
		// lookup the java system properties
        property = System.getProperty(key)
        if (property != null) {
            return property
        }
		// lookup the OS system variables
        property = getenv(key)
        if (property != null) {
            return property
        }
		// lookup the file properties
        applicationPropertiesFile.getProperty(key, defaultValue)
    }

    static String stringProperty(String key) {
        stringProperty(key, null)
    }

    static String setSystemStringProperty(String key, String value) {
        System.setProperty(key, value)
    }

    static void setThreadStringProperty(String key, String value) {
        threadLocalProperties.get().put(key,value);
    }

    // Integer properties

    static Integer intProperty(String key) {
        def property = stringProperty(key)
        property == null ? null : property.toInteger()
    }

    static int intProperty(String key, int defaultValue) {
        def property = stringProperty(key)
        property == null ? defaultValue : property.toInteger()
    }

    static void setIntProperty(String key, int value) {
        System.setProperty(key, "${value}")
    }

    // Long properties

    static long longProperty(String key, long defaultValue) {
        def property = stringProperty(key)
        property == null ? defaultValue : property.toLong()
    }

    static Optional<Long> longProperty(String key) {
        def property = stringProperty(key)
        property == null ? Optional.<Long>empty() : Optional.of(property.toLong())
    }

    static void setLongProperty(String key, long value) {
        System.setProperty(key, "${value}")
    }

    // Boolean properties

    static Optional<Boolean> booleanProperty(String key) {
        def property = stringProperty(key)
        property == null ? Optional.<Boolean>empty() : Optional.of(property.toBoolean())
    }

    static boolean booleanProperty(String key, boolean defaultValue) {
        def property = stringProperty(key)
        property == null ? defaultValue : property.toBoolean()
    }

    static void setSystemBooleanProperty(String key, boolean value) {
        System.setProperty(key, "${value}")
    }

    static void setThreadBooleanProperty(String key, boolean value) {
        setThreadStringProperty(key,"${value}")
    }
	
	// Import/export

    static void saveSystemProperties() {
        propertiesSnapshot = new JProperties()
        propertiesSnapshot.putAll(System.getProperties())
    }

    static void restoreSystemProperties() {
        System.setProperties(propertiesSnapshot)
    }

	// ThreadLocal properties
	
	static void setThreadIntProperty(String key, int value) {
		setThreadStringProperty(key,"${value}")
	}

	static void setThreadLongProperty(String key, long value) {
		setThreadStringProperty(key,"${value}")
	}

}
