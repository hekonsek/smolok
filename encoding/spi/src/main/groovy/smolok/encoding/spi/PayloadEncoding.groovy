package smolok.encoding.spi;

/**
 * Encoding can encode and decode payload sent over event bus.
 */
interface PayloadEncoding {

    byte[] encode(Object payload)

    Object decode(byte[] payload)

}