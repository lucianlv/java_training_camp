/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geektimes.projects.user.web.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * Spring Security 配置类
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 * Date : 2021-04-24
 */
@Configuration
@Order(9999)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        System.out.println("SecurityConfig#configure httpSecurity："+httpSecurity);
        httpSecurity
                .authorizeRequests() // 授权配置
                .antMatchers("/hello/world")
                .authenticated();//需要认证
    }


    /**
     * 重写WebSecurityConfigurerAdapter#init方法。
     * 由于重写WebSecurityConfigurerAdapter#init方法中每次获取HttpSecurity时都是要重新new一个：
     * this.http = new HttpSecurity(this.objectPostProcessor, this.authenticationBuilder, sharedObjects);
     * 所以重写的目的是为了在init方法中只new一次，然后放到WebSecurity的共享变量中，其他SecurityConfig的init方法中从
     * WebSecurity中获取共享变量（HttpSecurity）即可
     * @param web
     * @throws Exception
     */
    @Override
    public void init(WebSecurity web) throws Exception {
        HttpSecurity http = web.getSharedObject(HttpSecurity.class);
        if(http==null){
            http = getHttp();
            web.setSharedObject(HttpSecurity.class,http);
        }
        HttpSecurity finalHttp = http;
        web.addSecurityFilterChainBuilder(http).postBuildAction(new Runnable() {
            public void run() {
                FilterSecurityInterceptor securityInterceptor = finalHttp
                        .getSharedObject(FilterSecurityInterceptor.class);
                web.securityInterceptor(securityInterceptor);
            }
        });
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
//        webSecurity.securityInterceptor()
    }
}
