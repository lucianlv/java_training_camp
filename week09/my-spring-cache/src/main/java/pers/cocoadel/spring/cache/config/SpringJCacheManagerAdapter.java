package pers.cocoadel.spring.cache.config;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

public class SpringJCacheManagerAdapter implements CacheManager {

    private final CacheManager cacheManager;

    public SpringJCacheManagerAdapter(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public CachingProvider getCachingProvider() {
        return cacheManager.getCachingProvider();
    }

    @Override
    public URI getURI() {
        return cacheManager.getURI();
    }

    @Override
    public ClassLoader getClassLoader() {
        return cacheManager.getClassLoader();
    }

    @Override
    public Properties getProperties() {
        return cacheManager.getProperties();
    }

    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException {
        return cacheManager.createCache(cacheName, configuration);
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        return cacheManager.getCache(cacheName, keyType, valueType);
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        Cache<K, V> cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            MutableConfiguration<Object, Object> config =
                    new MutableConfiguration<>()
                            .setTypes(Object.class,Object.class);
            cache = (Cache<K, V>) createCache(cacheName, config);
        }
        return cache;
    }

    @Override
    public Iterable<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }

    @Override
    public void destroyCache(String cacheName) {
        cacheManager.destroyCache(cacheName);
    }

    @Override
    public void enableManagement(String cacheName, boolean enabled) {
        cacheManager.enableManagement(cacheName, enabled);
    }

    @Override
    public void enableStatistics(String cacheName, boolean enabled) {
        cacheManager.enableStatistics(cacheName, enabled);
    }

    @Override
    public void close() {
        cacheManager.close();
    }

    @Override
    public boolean isClosed() {
        return cacheManager.isClosed();
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return cacheManager.unwrap(clazz);
    }
}
