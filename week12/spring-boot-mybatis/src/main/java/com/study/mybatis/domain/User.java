package com.study.mybatis.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private Long id;

    private String name;

    private String password;

    private String email;

    private String phoneNumber;
}