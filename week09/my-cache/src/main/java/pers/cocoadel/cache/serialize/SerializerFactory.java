package pers.cocoadel.cache.serialize;

public interface SerializerFactory {
    <T> Serializer<T> getKeySerializer(Class<T> type);

    <T> Serializer<T> getValueSerializer(Class<T> type);
}
