# 第9周作业

代码分支： week-09


## 功能

##### Spring Cache 与 Redis 整合（完成）
- 如何清除某个 Spring Cache 所有的 Keys 关联的对象
  - 如果 Redis 中心化方案，Redis + Sentinel
  - 如果 Redis 去中心化方案，Redis Cluster
- 如何将 RedisCacheManager 与 @Cacheable 注解打通



##### 实现：

1、这里我是直接整合了之前实现的 my-cache 模块，由于之前 my-cache 模块集成 redis 功能的时候，我使用的是 hash 的数据结构，把一个 cache-name 的所有 key -value 都存储在一个 hash 中，需要 clear 的时候，直接把hash 删除即可。感觉能简单满足作用的要求，就直接使用了。



2、对于第二个要求，实现步骤如下

- 适配 Javax cacheManager 和 spring cacheManager，总体差别不大，主要是适配下面这方法：

  ```java
  // JSR 中该方法如果没有创建相应的 cache 会返回 null，创建 cache 的 api 是createCache
  // 但是 spring 的JCacheMangager 把cretate 和 get 都统一到 get 方法中。
  // 因此之前实现的 Jsr 的 cache Manager 中需要适配一下该方法，其他的方法直接委派即可。
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
  ```

- 配置相应的 javax.cache.CacheManager Bean

  ```java
  @Bean
  public CacheManager jCacheCacheManager(){
      CachingProvider cachingProvider = Caching.getCachingProvider();
      CacheManager cacheManager =
          cachingProvider.getCacheManager(URI.create("redis://127.0.0.1:6379/"), null);
      return new SpringJCacheManagerAdapter(cacheManager);
  }
  ```

- 由于之前序列化这些功能已经做了，这里不需要再做其他处理。



#### 测试：

- 测试模块： my-spring-cache

- 测试接口：

  - 缓存

    ```java
    //key 是 name 参数，返回字符串后面会跟着第一次返回的时间，页面可以根据这个时间来判断是否调用缓存。
    @GetMapping("/hello")
    @Cacheable(cacheNames = "hello", key = "#name")
    public String hello(String name) {
        System.out.println("invoked hello method!!!!");
        return "hello! " + name + " now is " + new Date().toString();
    }
    ```

    

  - 清除缓存

- 可以再 idea 中直接启动程序。

- 直接访问测试接口：

  - http://localhost:8080/hello?name=123  http://localhost:8080/hello?name=124  http://localhost:8080/hello?name=125
  - 多个name 参数 生成多个key 的缓存。
  - 每个 url 多次访问，看看返回时间是否一样，如果一样表示调用了缓存。

- http://localhost:8080/clear 会清除上面所有的缓存，通过返回时间来判断是否方法被重新调用。



