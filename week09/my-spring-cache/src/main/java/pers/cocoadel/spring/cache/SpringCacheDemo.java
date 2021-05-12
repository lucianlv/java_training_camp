package pers.cocoadel.spring.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class SpringCacheDemo {

    public static void main(String[] args) {
        SpringApplication.run(SpringCacheDemo.class,args);
    }
}
