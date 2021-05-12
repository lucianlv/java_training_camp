package pers.cocoadel.demo.rest;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;


@RestController
@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    @GetMapping("/hello/world")
    public String hello() {
        return "hello world";
    }

    @PostMapping(value = "/post/json")
    public String addProps(@RequestBody Properties properties) {
        String res = properties.toString();
        System.out.println(res);
        return res;
    }

    @PostMapping(value = "/post/form")
    public String addProps(HttpServletRequest request) {
        Properties properties = new Properties();
        properties.setProperty("name", request.getParameter("name"));
        properties.setProperty("age", request.getParameter("age"));
        String res = properties.toString();
        System.out.println(res);
        return res;
    }
}
