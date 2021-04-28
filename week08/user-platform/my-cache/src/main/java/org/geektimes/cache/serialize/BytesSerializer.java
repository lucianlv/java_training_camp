package org.geektimes.cache.serialize;

import javax.cache.CacheException;
import java.io.*;

/**
 * @Auther: liuyj
 * @Date: 2021/04/13/11:57
 * @Description:
 */
public class BytesSerializer implements  Serializer<Object,byte[]>{
    @Override
    public byte[] serialize(Object source) {
        byte[] bytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            // Key -> byte[]
            objectOutputStream.writeObject(source);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CacheException(e);
        }
        return bytes;
    }
    @Override
    public Object deserialize(byte[] target) {
        if (target == null) {
            return null;
        }
        Object value = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(target);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            // byte[] -> Value
            value = objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException(e);
        }
        return value;
    }
}
