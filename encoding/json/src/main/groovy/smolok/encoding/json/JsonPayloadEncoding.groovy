package smolok.encoding.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper
import smolok.encoding.spi.PayloadEncoding;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL

class JsonPayloadEncoding implements PayloadEncoding {

    public static final String ENVELOPE = 'payload'

    private final ObjectMapper objectMapper

    // Constructors

    JsonPayloadEncoding(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }

    JsonPayloadEncoding() {
        this(new ObjectMapper().setSerializationInclusion(NON_NULL))
    }

    // Encoding operations

    @Override
    byte[] encode(Object payload) {
        try {
            Map<String, Object> wrappedPayload = new HashMap<>();
            wrappedPayload.put(ENVELOPE, payload);
            return objectMapper.writeValueAsBytes(wrappedPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    Object decode(byte[] payload) {
        try {
            return objectMapper.readValue(payload, Map.class).get(ENVELOPE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}