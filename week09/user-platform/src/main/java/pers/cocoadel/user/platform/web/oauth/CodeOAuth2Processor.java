package pers.cocoadel.user.platform.web.oauth;

import pers.cocoadel.user.platform.domain.OAuthUserInfo;

/**
 * OAuth2 + 授权码
 */
public interface CodeOAuth2Processor {

    String doRequestAccessToken(String code);

    OAuthUserInfo doRequestOAuthUserInfo(String accessToken);

    String getRedirectUri();

    String getOauthUri();

    boolean isMatch(String oAuthServiceName);
}
