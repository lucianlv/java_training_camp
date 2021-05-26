package com.study.mybatis.test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.mybatis.EnableMyBatisExample;
import com.study.mybatis.domain.User;
import com.study.mybatis.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FactoriesApplicationTests {

    @Test
    public void findSingle() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EnableMyBatisExample.class);
        UserMapper userMapper = context.getBean(UserMapper.class);
        User user = null;
        try {
            user = userMapper.findUserById(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(user);
        context.close();
    }

    @Test
    public void findAll() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EnableMyBatisExample.class);
        UserMapper userMapper = context.getBean(UserMapper.class);
        try {
            List<User> list = userMapper.findList();
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.close();
    }

    @Test
    public void findPage() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EnableMyBatisExample.class);
        UserMapper userMapper = context.getBean(UserMapper.class);
        PageHelper.startPage(0, 3);
        try {
            List<User> list = userMapper.findList();
            PageInfo<User> page = new PageInfo<>(list);
            List<User> users = page.getList();
            for (User user : users) {
                System.out.println(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.close();
    }
}