package pers.cocoadel.cache;

import javafx.util.Pair;
import org.junit.Test;
import pers.cocoadel.cache.event.TestCacheEntryListener;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static pers.cocoadel.cache.configuration.ConfigurationUtils.cacheEntryListenerConfiguration;

public class ConcurrentCacheTest {

    @Test
    public void testSampleRedis() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create("redis://127.0.0.1:6379/"), null);
        // configure the cache
        MutableConfiguration<String, Integer> config =
                new MutableConfiguration<String, Integer>()
                        .setTypes(String.class, Integer.class);

        // create the cache
        Cache<String, Integer> cache = cacheManager.createCache("redisCache", config);

        // add listener
        cache.registerCacheEntryListener(cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));
        String key = "redis-key";
        Integer value1 = 0;
        cache.put(key, value1);
//        for(int i = 0; i < 2; i++){
//            new Thread(() -> {
//                int v = cache.get(key);
//                cache.put(key,v + 1);
//            }).start();
//        }
        System.out.println("value: " + cache.get(key));
    }
}
