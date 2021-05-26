package com.study.mybatis.factory;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

public class MyObjectFactory extends DefaultObjectFactory {

    @Override
    public <T> T create(Class<T> type) {
        System.out.println(type.getSimpleName() + "正在被创建...");
        return super.create(type);
    }
}