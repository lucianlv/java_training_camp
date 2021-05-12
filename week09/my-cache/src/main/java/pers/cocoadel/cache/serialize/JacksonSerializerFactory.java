package pers.cocoadel.cache.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ConcurrentHashMap;

public class JacksonSerializerFactory implements SerializerFactory {

    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<Class<?>, Serializer<?>> keySerializerMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<?>, Serializer<?>> valueSerializerMap = new ConcurrentHashMap<>();

    public JacksonSerializerFactory() {
        this.objectMapper = new ObjectMapper();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Serializer<T> getKeySerializer(Class<T> type) {
        return (Serializer<T>) keySerializerMap
                .computeIfAbsent(type, k -> new JacksonSerializer<>(type, objectMapper));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Serializer<T> getValueSerializer(Class<T> type) {
        return (Serializer<T>) valueSerializerMap
                .computeIfAbsent(type, k -> new JacksonSerializer<>(type, objectMapper));
    }
}
