package com.study.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@EnableMyBatis(dataSource = "dataSource",
        configLocation = "classpath:/META-INF/mybatis/mybatis-config.xml",
        mapperLocations = {"classpath:/META-INF/mybatis/mapper/*.xml"},
        environment = "development",
        plugins = {"com.github.pagehelper.PageInterceptor"},
        objectFactory = "com.study.mybatis.factory.MyObjectFactory",
        typeAliasesPackage = "com.study.mybatis.domain")
@PropertySource("classpath:/META-INF/mybatis/jdbc.properties")
@MapperScan(basePackages = "com.study.mybatis.mapper")
public class EnableMyBatisExample {

    @Bean
    public DataSource dataSource(@Value("${jdbc.mysql.driver}") String driverClass, @Value("${jdbc.mysql.url}") String url, @Value("${jdbc.mysql.username}") String username, @Value("${jdbc.mysql.password}") String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}