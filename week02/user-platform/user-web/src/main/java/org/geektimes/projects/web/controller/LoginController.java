package org.geektimes.projects.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.geektimes.projects.domain.User;
import org.geektimes.projects.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

@Path("/login")
public class LoginController implements PageController {

    @Resource(name = "bean/UserService")
    private UserService userService;

    @Override
    @GET
    @POST
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String method = request.getMethod();

        if ("POST" == method) {
            String password = request.getParameter("password");
            String email = request.getParameter("email");

            User user = new User();
            user.setEmail(email);
            user.setPassword(password);

            User loginUser = userService.login(user);
            if (null == loginUser) {
                request.setAttribute("err_msg", "登录失败");
                return "login.jsp";
            } else {
                request.setAttribute("loginUser", loginUser);
                return "success.jsp";
            }
        }

        return "login.jsp";
    }
}
