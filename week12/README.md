### 第12周作业

#### 简介

springboot 整合 自定义mybatis starter

##### 1、项目说明

```
spring-boot-mybatis 为基础项目,业务逻辑代码在此实现,需引入starter
factories-mybatis 为自定义mybatis starter
```

```xml
<!--引入mybatis starter-->
<dependency>
    <groupId>com.study</groupId>
    <artifactId>factories-mybatis</artifactId>
    <version>${mybatis.starter.version}</version>
</dependency>
```

