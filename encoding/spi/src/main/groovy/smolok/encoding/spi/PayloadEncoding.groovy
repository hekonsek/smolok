package smolok.encoding.spi;

interface PayloadEncoding {

    byte[] encode(Object payload)

    Object decode(byte[] payload)

}