package org.geektimes.cache.redis.lettuce;

import io.lettuce.core.codec.RedisCodec;
import java.io.Serializable;
import java.nio.ByteBuffer;
import org.geektimes.cache.serializer.ByteArraySerializer;

public class GenericRedisFormat<K extends Serializable, V extends Serializable> implements RedisCodec<K, V> {

    private static final byte[] EMPTY = new byte[0];
    private final ByteArraySerializer<K> byteArraySerializer = new ByteArraySerializer<>();

    public GenericRedisFormat() {
    }

    @Override
    public K decodeKey(ByteBuffer buffer) {
        return (K) deCode(buffer);
    }

    @Override
    public V decodeValue(ByteBuffer buffer) {
        return (V) deCode(buffer);
    }

    @Override
    public ByteBuffer encodeKey(K key) {
        return encode(key);
    }


    @Override
    public ByteBuffer encodeValue(V value) {
        return encode(value);
    }

    private Object deCode(ByteBuffer buffer) {
        int remaing = buffer.remaining();
        if (remaing == 0) {
            return null;
        }
        byte[] bytes = new byte[remaing];
        buffer.get(bytes);
        return byteArraySerializer.deserialize(bytes);
    }


    private ByteBuffer encode(Object obj) {
        if (obj == null) {
            return ByteBuffer.wrap(EMPTY);
        }
        byte[] bytes = byteArraySerializer.serialize((K) obj);
        return ByteBuffer.wrap(bytes);
    }

}
