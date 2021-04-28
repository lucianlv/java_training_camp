package org.geektimes.cache.redis.lettuce;

import io.lettuce.core.RedisClient;
import org.geektimes.cache.AbstractCacheManager;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

/**
 * @Auther: liuyj
 * @Date: 2021/04/13/13:50
 * @Description:
 */
public class LettuceCacheManager extends AbstractCacheManager {

    private final  RedisClient lettuceClient;

    public LettuceCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
        this.lettuceClient = RedisClient.create(uri.toString());
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        Cache cache = new LettuceCache(this,cacheName,configuration,lettuceClient);
        return cache;
    }

    @Override
    protected void doClose() {
        lettuceClient.shutdown();
    }
}
