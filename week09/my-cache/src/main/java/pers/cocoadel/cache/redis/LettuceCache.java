package pers.cocoadel.cache.redis;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import pers.cocoadel.cache.AbstractConfigurableSerializerCache;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LettuceCache<K extends Serializable,V extends Serializable> extends AbstractConfigurableSerializerCache<K,V> {

    private final StatefulRedisConnection<byte[], byte[]> connection;
    private final RedisCommands<byte[], byte[]> commands;

    private final byte[] hashKey;

    protected LettuceCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration,
                           StatefulRedisConnection<byte[], byte[]> connection) {
        super(cacheManager, cacheName, configuration);
        hashKey = cacheName.getBytes();
        this.connection = connection;
        connection.setAutoFlushCommands(true);
        commands = connection.sync();
    }

    @Override
    protected void clearEntries() throws CacheException {
        Set<byte[]> keySet = serializedKeySet();
        if (keySet.isEmpty()) {
            return;
        }
        commands.hdel(hashKey,keySet.toArray(new byte[keySet.size()][]));
    }

    @Override
    protected boolean containsEntry(byte[] key) throws CacheException, ClassCastException {
        return commands.hexists(hashKey,key);
    }

    @Override
    protected byte[] getSerializedData(byte[] key) throws CacheException, ClassCastException {
        return commands.hget(hashKey, key);
    }

    @Override
    protected void putSerializedData(byte[] key, byte[] data) {
        commands.hset(hashKey, key, data);
    }

    @Override
    protected byte[] removeSerializedData(byte[] key) throws CacheException, ClassCastException {
        byte[] value = getSerializedData(key);
        if (commands.hdel(hashKey, key) == 1) {
            return value;
        }
        throw new CacheException("remove failure");
    }

    @Override
    protected Set<byte[]> serializedKeySet() {
        List<byte[]> keys = commands.hkeys(hashKey);
        if (keys == null || keys.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(keys);
    }

    @Override
    protected void doClose() {
        connection.close();
    }
}
