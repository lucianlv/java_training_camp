package org.geektimes.cache.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.cache.CacheException;

public class ByteArraySerializer<K extends Serializable> implements CacheSerializer<K, byte[]> {

    // 是否可以抽象出一套序列化和反序列化的 API
    @Override
    public byte[] serialize(K obj) throws CacheException {
        byte[] bytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CacheException(e);
        }
        return bytes;
    }

    @Override
    public K deserialize(byte[] bytes) throws CacheException {
        if (bytes == null) {
            return null;
        }
        Object obj = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            obj = objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException(e);
        }

        return (K) obj;
    }
}
