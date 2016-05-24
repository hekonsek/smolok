package smolok.eventbus.client

/**
 * Utility class to make it easier to attach headers to the messages sent to the IoT Connector.
 */
public class Header {

    // Members

    private final String key;

    private final Object value;

    // Constructors

    public Header(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    // Factory methods

    public static Header header(String key, Object value) {
        return new Header(key, value);
    }

    public static Header[] arguments(Object... arguments) {
        Header[] headers = new Header[arguments.length];
        for(int i = 0; i < arguments.length; i++) {
            headers[i] = header("RHIOT_ARG" + i, arguments[i]);
        }
        return headers;
    }

    public static Map<String, Object> arguments(Header[] arguments) {
        Map<String, Object> collectedHeaders = new HashMap<>();
        for(Header header : arguments) {
            collectedHeaders.put(header.key(), header.value());
        }
        return collectedHeaders;
    }

    public String key() {
        return key;
    }

    public Object value() {
        return value;
    }

}
