package org.geektimes.projects.user.web.controller;

import org.geektimes.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.service.UserServiceImpl;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @Auther: liuyj
 * @Date: 2021/03/01/11:46
 * @Description:
 */
@Path("/user")
public class UserController implements PageController {

//    @Resource(name = "bean/UserService")
//    private UserService userService;

    @GET
    @Path("/toSignUp")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return "WEB-INF/jsp/user/signUp.jsp";
    }

    @POST
    @Path("/signUp")
    public String signUp(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        User user = new User();
        user.setId((long)((Math.random() * 100000) % 100));
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        //依赖查找
        UserService userService = ComponentContext.getInstance().getComponent("bean/UserService");
        String msg="成功";
        try {
            userService.register(user);
        }catch (Throwable e){
            msg=e.getMessage();
        }
        request.setAttribute("msg",msg);
        return "WEB-INF/jsp/user/signUpResult.jsp";
    }

}
