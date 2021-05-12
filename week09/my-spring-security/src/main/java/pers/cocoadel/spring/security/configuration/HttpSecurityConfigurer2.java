package pers.cocoadel.spring.security.configuration;

import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;

//@Order(Ordered.HIGHEST_PRECEDENCE + 1)
//@Configuration
public class HttpSecurityConfigurer2 implements SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> {


    @Override
    public void init(HttpSecurity http) throws Exception {
        System.out.println("HttpSecurityConfigurer2 2 load HttpSecurity");
        FormLoginConfigurer<HttpSecurity> httpSecurityFormLoginConfigurer = http.formLogin();
        httpSecurityFormLoginConfigurer.defaultSuccessUrl("/hello/2");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

    }
}
