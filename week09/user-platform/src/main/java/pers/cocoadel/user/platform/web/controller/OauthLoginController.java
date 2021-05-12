package pers.cocoadel.user.platform.web.controller;

import pers.cocoadel.configuration.sources.servlet.ConfigRequestContext;
import pers.cocoadel.user.platform.domain.OAuthUserInfo;
import pers.cocoadel.user.platform.web.oauth.CodeOAuth2Processor;
import pers.cocoadel.user.platform.web.oauth.CodeOAuthProcessorFactory;
import pers.cocoadel.user.platform.web.oauth.OauthConstant;
import pres.cocoadel.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static pers.cocoadel.user.platform.web.oauth.OauthConstant.ACCESS_TOKEN_KEY;

/**
 * 第三方授权登录
 */
@Path("")
public class OauthLoginController implements PageController {

    @Path("/oauth/login")
    @GET
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String oAuthServiceName = request.getParameter("oauth_service");
        CodeOAuth2Processor codeOAuth2Processor = CodeOAuthProcessorFactory.getCodeOAuth2Processor(oAuthServiceName);
        HttpSession session = request.getSession();
        //还没有第三方授权
        Object accessToken = session.getAttribute(ACCESS_TOKEN_KEY);
        if (accessToken == null) {
            String oauthUrl = codeOAuth2Processor.getOauthUri();
            response.sendRedirect(oauthUrl);
            return "";
        }
        //已经授权（暂时没做过期处理），获取用户的 gitee 信息，然后跳转到登录成功页面。
        OAuthUserInfo authUserInfo = (OAuthUserInfo) session.getAttribute(OauthConstant.AUTH_USER_INFO_KEY);
        String appName = ConfigRequestContext.INSTANCE.get().getValue("application.name", String.class);
        String message = "登录成功 欢迎 " + authUserInfo.getName() + " 来到 " + appName;
        request.setAttribute("message", message);
        return "login-success.jsp";
    }
}
