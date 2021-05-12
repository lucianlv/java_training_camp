package pers.cocoadel.cache.redis;

import pers.cocoadel.cache.AbstractConfigurableSerializerCache;
import redis.clients.jedis.Jedis;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.*;
import java.util.Set;

public class JedisCache<K extends Serializable, V extends Serializable> extends AbstractConfigurableSerializerCache<K,V> {

    private final Jedis jedis;

    private final byte[] hashKey;

    public JedisCache(CacheManager cacheManager, String cacheName,
                      Configuration<K, V> configuration, Jedis jedis) {
        super(cacheManager, cacheName, configuration);
        hashKey = cacheName.getBytes();
        this.jedis = jedis;
    }

    @Override
    protected void clearEntries() throws CacheException {
        Set<byte[]> keySet = serializedKeySet();
        if (keySet.isEmpty()) {
            return;
        }
        jedis.hdel(hashKey,keySet.toArray(new byte[keySet.size()][]));
    }

    @Override
    protected boolean containsEntry(byte[] key) throws CacheException, ClassCastException {
        return jedis.hexists(hashKey,key);
    }

    @Override
    protected byte[] getSerializedData(byte[] key) throws CacheException, ClassCastException {
        return jedis.hget(hashKey, key);
    }

    @Override
    protected void putSerializedData(byte[] key, byte[] data) {
        jedis.hset(hashKey, key, data);
    }

    @Override
    protected byte[] removeSerializedData(byte[] key) throws CacheException, ClassCastException {
        byte[] value = getSerializedData(key);
        if (jedis.hdel(hashKey, key) == 1) {
            return value;
        }
        throw new CacheException("remove failure");
    }

    @Override
    protected Set<byte[]> serializedKeySet() {
        return jedis.hkeys(hashKey);
    }

    @Override
    protected void doClose() {
        jedis.close();
    }
}
