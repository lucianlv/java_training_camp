package pers.cocoadel.spring.cache.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URI;

@Configuration
public class JCacheConfig {

    @Bean
    public CacheManager jCacheCacheManager(){
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager =
                cachingProvider.getCacheManager(URI.create("redis://127.0.0.1:6379/"), null);
        return new SpringJCacheManagerAdapter(cacheManager);
    }
}
