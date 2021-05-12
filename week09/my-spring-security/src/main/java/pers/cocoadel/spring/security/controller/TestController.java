package pers.cocoadel.spring.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello/0")
    public String hello0() {
        return "hello ! I am 0";
    }

    @GetMapping("/hello/1")
    public String hello1() {
        return "hello ! I am 1";
    }

    @GetMapping("/hello/2")
    public String hello2() {
        return "hello ! I am 2";
    }

    @GetMapping("/logout_ok")
    public String logout() {
        return "logout_ok !!!!!";
    }
}
