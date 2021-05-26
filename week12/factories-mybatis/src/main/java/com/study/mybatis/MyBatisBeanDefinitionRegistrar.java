package com.study.mybatis;

import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class MyBatisBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder beanDefinitionBuilder = genericBeanDefinition(SqlSessionFactoryBean.class);

        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableMyBatis.class.getName());
        /**
         *  <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
         *   <property name="dataSource" ref="dataSource" />
         *   <property name="mapperLocations" value="classpath*:" />
         *  </bean >
         */
        try {
            beanDefinitionBuilder.addPropertyReference("dataSource", (String) attributes.get("dataSource"));
            // Spring String 类型可以自动转化 Spring Resource
            beanDefinitionBuilder.addPropertyValue("configLocation", attributes.get("configLocation"));
            beanDefinitionBuilder.addPropertyValue("mapperLocations", attributes.get("mapperLocations"));
            beanDefinitionBuilder.addPropertyValue("environment", resolvePlaceholder(attributes.get("environment")));
            // 自行添加其他属性
            List<Interceptor> list = new ArrayList<>();
            String[] array = (String[]) attributes.get("plugins");
            for (String str : array) {
                Class clazz = Class.forName(str);
                Interceptor interceptor = (Interceptor) clazz.newInstance();
                Properties properties = new Properties();
                interceptor.setProperties(properties);
                list.add(interceptor);
            }
            beanDefinitionBuilder.addPropertyValue("plugins", list.toArray());
            String factory = (String) attributes.get("objectFactory");
            Class clazz = Class.forName(factory);
            beanDefinitionBuilder.addPropertyValue("objectFactory", clazz.newInstance());
            String typeAliasesPackage = (String) attributes.get("typeAliasesPackage");
            beanDefinitionBuilder.addPropertyValue("typeAliasesPackage", typeAliasesPackage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // SqlSessionFactoryBean 的 BeanDefinition
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();

        String beanName = (String) attributes.get("value");
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private Object resolvePlaceholder(Object value) {
        if (value instanceof String) {
            return environment.resolvePlaceholders((String) value);
        }
        return value;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}