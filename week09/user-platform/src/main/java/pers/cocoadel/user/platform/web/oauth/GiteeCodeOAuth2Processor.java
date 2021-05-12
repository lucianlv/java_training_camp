package pers.cocoadel.user.platform.web.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import pers.cocoadel.user.platform.domain.OAuthUserInfo;
import pers.cocoadel.user.platform.exception.BusinessException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



/**
 * Gitee OAuth2 授权码处理
 */
public class GiteeCodeOAuth2Processor implements CodeOAuth2Processor{

    public static final String CLIENT_ID = "f6ebc4cfc8c3472292d36100837b07c56f5ca971b7ba58998c1a7af8224d9374";
    public static final String CLIENT_SECRET = "9f49b9f27ff4ad57a581217e45be64241d4223c51dd86f5bdea5a53f7310bc51";

    //请求 accessToken
    private static final String REQ_ACCESS_TOKEN_URL = "https://gitee.com/oauth/token";
    //请求用户信息
    private static final String REQ_OAUTH_USER_URL = "https://gitee.com/api/v5/user";

    //重定向
    private static final String REDIRECT_URI = "http://localhost:8080/oauth/call?oauth_service=gitee";
    //授权页面
    private static final String OAUTH_URL = String.format("https://gitee.com/oauth/authorize?" +
            "client_id=%s&redirect_uri=%s&response_type=code", CLIENT_ID, REDIRECT_URI);

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(2))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String doRequestAccessToken(String code) {
        try {
            //grant_type=authorization_code&code={code}&client_id={client_id}&redirect_uri={redirect_uri}&client_secret={client_secret}
            Map<String,String> form = new HashMap<>();
            form.put("code",code);
            form.put("grant_type","authorization_code");
            form.put("redirect_uri",REDIRECT_URI);
            form.put("client_id",CLIENT_ID);
            form.put("client_secret",CLIENT_SECRET);
            Request request = buildPostFormRequest(REQ_ACCESS_TOKEN_URL,form,headers());
            Response response = OK_HTTP_CLIENT.newCall(request).execute();
            if (response.isSuccessful()) {
                String json = response.body().string();
                Map map = objectMapper.readValue(json, Map.class);
                String accessToken = (String) map.get(OauthConstant.ACCESS_TOKEN_KEY);
                System.out.println("accessToken: " + accessToken);
                if (accessToken != null) {
                    return accessToken;
                }
                throw new BusinessException("req access token error: " + map.get("error"));
            }
            throw new BusinessException("req access token error: " + response.message() + " code: " + response.code());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OAuthUserInfo doRequestOAuthUserInfo(String accessToken) {
        String error = "req user info failure";
        try {
            Headers headers = headers();
            String url = REQ_OAUTH_USER_URL + "?access_token=" + accessToken;
            Request request = new Request.Builder().url(url).headers(headers).get().build();
            Response response = OK_HTTP_CLIENT.newCall(request).execute();
            if (response.isSuccessful()) {
                String json = response.body().string();
                System.out.println("user json:" + json);
                return objectMapper.readValue(json, OAuthUserInfo.class);
            }
            error += " code is " + response.code() + " error : " + response.message();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new BusinessException(error);
    }

    @Override
    public String getRedirectUri() {
        return REDIRECT_URI;
    }

    @Override
    public String getOauthUri() {
        return OAUTH_URL;
    }

    @Override
    public boolean isMatch(String oAuthServiceName) {
        return "gitee".equalsIgnoreCase(oAuthServiceName);
    }

    protected Headers headers() {
        return new Headers.Builder()
                .add("Accept", "application/json")
                .add("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                .build();
    }

    /**
     * 创建Post Form的okhttp3.Request
     *
     * @param url    post地址
     * @param params post的参数
     * @return okhttp3.Request
     */
    private okhttp3.Request buildPostFormRequest(String url, Map<String, String> params,Headers headers) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String values = entry.getValue();
                if (StringUtils.isBlank(key) || StringUtils.isBlank(values)) {
                    continue;
                }
                builder.add(key,values);
            }
        }
        RequestBody requestBody = builder.build();
        return new okhttp3.Request.Builder().headers(headers).url(url).post(requestBody).build();
    }
}
