package pers.cocoadel.user.platform.web.controller;

import pers.cocoadel.configuration.sources.servlet.ConfigRequestContext;
import pers.cocoadel.user.platform.service.UserService;
import pres.cocoadel.web.mvc.controller.PageController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
/**
 * 登录逻辑的处理
 */
@Path("")
public class LoginController implements PageController {

    private static final String ACCESS_TOKEN_KEY = "access_token";

    private static final String REDIRECT_URI = "http://localhost:8080/oauth/do";

    private static final String CLIENT_ID = "";


    private static final String OAUTH_URL = "https://gitee.com/oauth/authorize?" +
            "client_id=%s&redirect_uri=%s&response_type=code";

    @Resource(name = "bean/UserService")
    private UserService userService;

    @Path("/login")
    @POST
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String message = "";
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            if (userService.signIn(email, password)) {
                String appName = ConfigRequestContext.INSTANCE.get().getValue("application.name", String.class);
                message = "登录成功！ 欢迎 " + email + " 来到 " + appName;
                request.setAttribute("message", message);
                return "login-success.jsp";
            }
        } catch (Throwable e) {
            message = "登录失败：" + e.getMessage();
        }
//        return "login-success.jsp";
        request.setAttribute("message", message);
        return "login-form.jsp";
    }
}
