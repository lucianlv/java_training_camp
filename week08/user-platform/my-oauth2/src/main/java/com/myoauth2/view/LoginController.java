package com.myoauth2.view;

import com.alibaba.fastjson.JSONObject;
import com.myoauth2.domain.Oauth2Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @Auther: liuyj
 * @Date: 2021/04/20/16:44
 * @Description:
 */
@Slf4j
@Controller
public class LoginController {
    private final Oauth2Properties oauth2Properties;

    public LoginController(Oauth2Properties oauth2Properties) {
        this.oauth2Properties = oauth2Properties;
    }


    /**
     * 让用户跳转到 GitHub
     * 这里不能加@ResponseBody，因为这里是要跳转而不是返回响应
     * 另外LoginController也不能用@RestController修饰
     *
     * @return 跳转url
     */
    @GetMapping("/authorize")
    public String authorize() {
        String url = oauth2Properties.getAuthorizeUrl() +
                "?client_id=" + oauth2Properties.getClientId() +
                "&redirect_uri=" + oauth2Properties.getRedirectUrl()+
                "&response_type=code";
        log.info("授权url:{}", url);
        return "redirect:" + url;
    }

    /**
     * 回调接口，用户同意授权后，GitHub会重定向到此路径
     *
     * @param code GitHub重定向时附加的授权码，只能用一次
     * @return
     */
    @GetMapping("/oauth2/callback")
    public String callback(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
        log.info("code={}", code);
        // code换token
        String accessToken = getAccessToken(code);
        // token换userInfo
        String userInfo = getUserInfo(accessToken);
        log.info("重定向到home");
        redirectAttributes.addAttribute("userInfo", userInfo);
        return "redirect:/home";
    }

    @GetMapping("/home")
    @ResponseBody
    public String home(String userInfo) {
        return "hello world：" +userInfo;
    }

    private String getAccessToken(String code) {
        String url = oauth2Properties.getAccessTokenUrl() +
                "?client_id=" + oauth2Properties.getClientId() +
                "&client_secret=" + oauth2Properties.getClientSecret() +
                "&code=" + code +
                "&grant_type=authorization_code"+
                "&redirect_uri=" + oauth2Properties.getRedirectUrl();
        log.info("getAccessToken url:{}", url);
        // 构建请求头
        HttpHeaders requestHeaders = new HttpHeaders();
        // 指定响应返回json格式
//        requestHeaders.add("accept", "application/json");
        // 构建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        // post 请求方式
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        String responseStr = response.getBody();
        log.info("responseStr={}", responseStr);

        // 解析响应json字符串
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        String accessToken = jsonObject.getString("access_token");
        log.info("accessToken={}", accessToken);
        return accessToken;
    }

    private String getUserInfo(String accessToken) {
        String url = oauth2Properties.getUserInfoUrl();
        log.info("getUserInfo url:{}", url);
        // 构建请求头
        HttpHeaders requestHeaders = new HttpHeaders();
        // 指定响应返回json格式
        requestHeaders.add("accept", "application/json");
        // AccessToken放在请求头中
        requestHeaders.add("Authorization", "token " + accessToken);
        // 构建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        // get请求方式
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String userInfo = response.getBody();
        log.info("userInfo={}", userInfo);
        return userInfo;
    }
}

