package org.geektimes.cache.redis.lettuce;

import io.lettuce.core.RedisClient;
import java.net.URI;
import java.util.Properties;
import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import org.geektimes.cache.AbstractCacheManager;

public class LettuceCacheManager extends AbstractCacheManager {

    private final RedisClient redisClient;


    public LettuceCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
        this.redisClient = RedisClient.create(uri.toString());
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        return new LettuceCache(this, cacheName, configuration, redisClient);
    }

    @Override
    protected void doClose() {
        redisClient.shutdown();
    }

}
