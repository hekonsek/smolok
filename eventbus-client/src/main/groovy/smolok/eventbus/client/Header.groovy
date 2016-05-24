package smolok.eventbus.client

/**
 * Header which can be attached to the messages sent to the Event Bus.
 */
class Header {

    // Members

    private final String key

    private final Object value

    // Constructors

    Header(String key, Object value) {
        this.key = key
        this.value = value
    }

    // Factory methods

    static Header header(String key, Object value) {
        new Header(key, value)
    }

    static Header[] arguments(Object... arguments) {
        arguments.toList().withIndex().collect{ arg, i -> header("SMOLOK_ARG${i}", arguments[i]) }
    }

    public static Map<String, Object> arguments(Header[] arguments) {
        Map<String, Object> collectedHeaders = new HashMap<>();
        for(Header header : arguments) {
            collectedHeaders.put(header.key(), header.value());
        }
        return collectedHeaders;
    }

    // Getters

    String key() {
        key
    }

    Object value() {
        value
    }

}
