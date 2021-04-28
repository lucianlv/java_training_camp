# 作业：如何解决多个 WebSecurityConfigurerAdapter Bean 配置相互冲突的问题？

​	提示：假设有两个 WebSecurityConfigurerAdapter Bean 定义，并且标注了不同的 @Order，其中一个关闭 CSRF，一个开启 CSRF，那么最终结果如何确定？背景：Spring Boot 场景下，自动装配以及自定义 Starter 方式非常流行，部分开发人员掌握了 Spring Security 配置方法，并且自定义了自己的实现，解决了 Order 的问题，然而会出现不确定配置因素。



## 修改my-oauth2模块用来测试

添加com.myoauth2.security.SecurityConfig和com.myoauth2.security.SecurityConfig2配置类，（主要是重写了init方法）代码如下：

```java
@Configuration
@Order(9999)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        System.out.println("SecurityConfig（9999）："+httpSecurity);
        httpSecurity
                .authorizeRequests() // 授权配置
                .antMatchers("/hello")
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
```

```java
@Configuration
@Order(10000)
public class SecurityConfig2 extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity httpSecurity) throws Exception {
        System.out.println("SecurityConfig（10000）："+httpSecurity);
        httpSecurity
                .authorizeRequests() // 授权配置
                .antMatchers("/hello")
                .permitAll();//不需要认证
    }



    @Override
    public void init(WebSecurity web) throws Exception {
        HttpSecurity http = web.getSharedObject(HttpSecurity.class);
        configure(http);
    }

    public void configure(WebSecurity webSecurity) throws Exception {
//        webSecurity.securityInterceptor()
    }
}
```



效果如下：

```xml
#启动后效果，HttpSecurity是不同对象
 SecurityConfig（9999）：org.springframework.security.config.annotation.web.builders.HttpSecurity@52d62bb3
SecurityConfig（10000）：org.springframework.security.config.annotation.web.builders.HttpSecurity@1f50acfb

#启动后效果，HttpSecurity是同一个对象
 SecurityConfig（9999）：org.springframework.security.config.annotation.web.builders.HttpSecurity@69f991b8
SecurityConfig（10000）：org.springframework.security.config.annotation.web.builders.HttpSecurity@69f991b8
```


