package pers.cocoadel.cache.serialize;

@SuppressWarnings("unchecked")
public class JavaSerializableFactory implements SerializerFactory{

    @SuppressWarnings("rawtypes")
    private final JavaSerializableSerializer serializer = new JavaSerializableSerializer();

    @Override
    public <T> Serializer<T> getKeySerializer(Class<T> type) {
        return serializer;
    }

    @Override
    public <T> Serializer<T> getValueSerializer(Class<T> type) {
        return serializer;
    }
}
