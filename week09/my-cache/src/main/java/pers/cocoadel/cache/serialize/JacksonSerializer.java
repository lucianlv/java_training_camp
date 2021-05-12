package pers.cocoadel.cache.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.cache.CacheException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JacksonSerializer<T> implements Serializer<T> {

    private final ObjectMapper mapper;

    private final Charset charset = StandardCharsets.UTF_8;

    private final Class<T> type;

    public JacksonSerializer(Class<T> type) {
        this.mapper = new ObjectMapper();
        this.type = type;
    }

    public JacksonSerializer(Class<T> type,ObjectMapper objectMapper) {
        this.mapper = objectMapper;
        this.type = type;
    }

    @Override
    public byte[] serialize(T src) {
        try {
            String json = mapper.writeValueAsString(src);
            return json.getBytes(charset);
        } catch (JsonProcessingException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public T parse(byte[] src) {
        try {
            return mapper.readValue(src, type);
        } catch (IOException e) {
            throw new CacheException(e);
        }
    }
}
