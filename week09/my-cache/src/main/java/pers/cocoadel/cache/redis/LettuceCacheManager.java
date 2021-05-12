package pers.cocoadel.cache.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import pers.cocoadel.cache.AbstractCacheManager;
import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Properties;


public class LettuceCacheManager extends AbstractCacheManager {

    private final RedisClient client;

    public LettuceCacheManager(CachingProvider cachingProvider,
                               URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
        client = RedisClient.create(RedisURI.create(uri));
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        StatefulRedisConnection<byte[], byte[]> connection = client.connect(new ByteArrayRedisCodec());
        return new LettuceCache(this, cacheName, configuration, connection);
    }

    @Override
    protected void doClose() {
        client.shutdown();
    }

    private static class ByteArrayRedisCodec implements RedisCodec<byte[], byte[]> {

        @Override
        public byte[] decodeKey(ByteBuffer bytes) {
            return decode(bytes);
        }

        @Override
        public byte[] decodeValue(ByteBuffer bytes) {
            return decode(bytes);
        }

        private byte[] decode(ByteBuffer byteBuffer) {
            int len = byteBuffer.remaining();
            if (len == 0) {
                return null;
            }
            if (byteBuffer.isDirect()) {
                byte[] bytes = new byte[len];
                byteBuffer.get(bytes);
                return bytes;
            }
            return byteBuffer.array();
        }

        @Override
        public ByteBuffer encodeKey(byte[] key) {
            return ByteBuffer.wrap(key);
        }

        @Override
        public ByteBuffer encodeValue(byte[] value) {
            return ByteBuffer.wrap(value);
        }
    }
}
