package pers.cocoadel.user.platform.web.controller;

import pers.cocoadel.user.platform.domain.OAuthUserInfo;
import pers.cocoadel.user.platform.exception.BusinessException;
import pers.cocoadel.user.platform.web.oauth.CodeOAuth2Processor;
import pers.cocoadel.user.platform.web.oauth.CodeOAuthProcessorFactory;
import pers.cocoadel.user.platform.web.oauth.OauthConstant;
import pres.cocoadel.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Path;

import static pers.cocoadel.user.platform.web.oauth.OauthConstant.*;

/**
 * OAuth2 的回调重定向
 */
@Path("")
public class OauthCallController implements PageController {
    //授权成功跳转
    private static final String LOGIN_URL = "http://localhost:8080/oauth/login";

    @Path("/oauth/call")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String oAuthServiceName = request.getParameter("oauth_service");
        CodeOAuth2Processor codeOAuth2Processor = CodeOAuthProcessorFactory.getCodeOAuth2Processor(oAuthServiceName);

        //重定向 url 带的参数：{redirect_uri}?code=abc&state=xyz
        String code = request.getParameter("code");
        if (code == null) {
            throw new BusinessException("Oauth code is null");
        }
        String accessToken = codeOAuth2Processor.doRequestAccessToken(code);
        OAuthUserInfo oAuthUserInfo = codeOAuth2Processor.doRequestOAuthUserInfo(accessToken);
        //创建 httpsession 保存 access_token 和授权用户信息
        HttpSession session = request.getSession();
        session.setAttribute(OauthConstant.ACCESS_TOKEN_KEY, accessToken);
        session.setAttribute(AUTH_USER_INFO_KEY, oAuthUserInfo);
        response.sendRedirect(LOGIN_URL + "?oauth_service=" + oAuthServiceName);
        return "";
    }
}
