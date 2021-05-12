package pers.cocoadel.spring.security.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Order(1000)
@Configuration
public class HttpSecurityConfigure1 implements SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) throws Exception {
        System.out.println("HttpSecurityConfigure1 load HttpSecurity");
        http.formLogin().defaultSuccessUrl("/hello/1");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

    }
}
