package pers.cocoadel.spring.cache.controller;

import ch.qos.logback.core.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TestController {

    @Autowired
    JCacheCacheManager jCacheCacheManager;

    @GetMapping("/hello")
    @Cacheable(cacheNames = "hello", key = "#name")
    public String hello(String name) {
        System.out.println("invoked hello method!!!!");
        return "hello! " + name + " now is " + new Date().toString();
    }

    @GetMapping("/clear")
    public String clear() {
        jCacheCacheManager.getCache("hello").clear();
        return "clear cache success";
    }
}
