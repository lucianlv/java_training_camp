package com.myoauth2.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: liuyj
 * @Date: 2021/04/20/16:44
 * @Description:
 */
@Data
@Component
@ConfigurationProperties(prefix = "gitee")
public class Oauth2Properties {
    private String clientId;
    private String clientSecret;
    private String authorizeUrl;
    private String redirectUrl;
    private String accessTokenUrl;
    private String userInfoUrl;
}
