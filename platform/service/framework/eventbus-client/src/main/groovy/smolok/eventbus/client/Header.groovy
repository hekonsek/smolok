package smolok.eventbus.client

/**
 * Header which can be attached to the messages sent to the Event Bus.
 */
class Header {

    // Constants

    public static final String SMOLOK_HEADER_PREFIX = 'SMOLOK_ARG'

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

    static Header[] arguments(Iterable<Object> arguments) {
        arguments.toList().withIndex().collect{ arg, i -> header(smolokHeaderKey(i), arguments[i]) }
    }

    static Header[] arguments(Object... args) {
        arguments(args.toList())
    }

    static Map<String, Object> arguments(Header[] arguments) {
        arguments.inject([:]) { result, it -> result[it.key] = it.value; result }
    }

    // Getters

    String key() {
        key
    }

    Object value() {
        value
    }

    // Static helpers

    static String smolokHeaderKey(int index) {
        "${SMOLOK_HEADER_PREFIX}${index}"
    }

}
