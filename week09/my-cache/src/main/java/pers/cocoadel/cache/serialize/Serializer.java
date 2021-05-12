package pers.cocoadel.cache.serialize;

public interface Serializer<T> {

    byte[] serialize(T src);

    T parse(byte[] src);
}
