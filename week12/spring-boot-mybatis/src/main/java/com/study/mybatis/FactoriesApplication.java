package com.study.mybatis;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FactoriesApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                //设置当前运行环境
                //不设置进行上下文推断
                .web(WebApplicationType.NONE)
                .sources(FactoriesApplication.class)
                .run(args);
    }
}