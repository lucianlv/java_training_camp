package com.study.mybatis.mapper;

import com.study.mybatis.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

    User findUserById(Integer id) throws Exception;

    List<User> findList();
}