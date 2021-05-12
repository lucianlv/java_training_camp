package pers.cocoadel.spring.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class DefaultWebSeurityConfigureAdapter extends WebSecurityConfigurerAdapter {

    private final List<SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity>> httpSecurityConfigurers;

    @Autowired
    public DefaultWebSeurityConfigureAdapter(List<SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity>> httpSecurityConfigurers) {
        this.httpSecurityConfigurers = httpSecurityConfigurers;
    }

    @Override
    public void init(WebSecurity web) throws Exception {
        HttpSecurity httpSecurity = getHttp();
        web.addSecurityFilterChainBuilder(httpSecurity).postBuildAction(() -> {
            FilterSecurityInterceptor securityInterceptor = httpSecurity.getSharedObject(FilterSecurityInterceptor.class);
            web.securityInterceptor(securityInterceptor);
        });
        if (httpSecurityConfigurers != null) {
            for (SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> configurer : httpSecurityConfigurers) {
                httpSecurity.apply(configurer);
            }
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .logout()
                .logoutSuccessUrl("/logout_ok")
                .and()
                .formLogin()
                .defaultSuccessUrl("/hello/0");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                // 定义加密方式，不然启动不了
                .passwordEncoder(new BCryptPasswordEncoder())
                // 设置用户名
                .withUser("admin")
                // 设置密码（密文密码）
                .password(new BCryptPasswordEncoder().encode("123456"))
                // 设置角色，不设置启动不了
                .roles("");
    }
}
