package smolok.encoding.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper
import smolok.encoding.spi.PayloadEncoding;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL

class JsonPayloadEncoding implements PayloadEncoding {

    private final ObjectMapper objectMapper;

    public JsonPayloadEncoding(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonPayloadEncoding() {
        this(new ObjectMapper().setSerializationInclusion(NON_NULL));
    }

    @Override
    public byte[] encode(Object payload) {
        try {
            Map<String, Object> wrappedPayload = new HashMap<>();
            wrappedPayload.put("payload", payload);
            return objectMapper.writeValueAsBytes(wrappedPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object decode(byte[] payload) {
        try {
            return objectMapper.readValue(payload, Map.class).get("payload");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}