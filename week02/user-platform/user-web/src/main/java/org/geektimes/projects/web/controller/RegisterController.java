package org.geektimes.projects.web.controller;

import java.util.logging.Logger;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.geektimes.projects.domain.User;
import org.geektimes.web.mvc.controller.PageController;

@Path("/register")
public class RegisterController implements PageController {

    private Logger logger = Logger.getLogger(RegisterController.class.getName());

    @Override
    @GET
    @POST
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String method = request.getMethod();

        if ("POST" == method) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");

            User user = new User(null, username, password, email, phone);
//            request.setAttribute("err_msg", "注册失败");

            return "login.jsp";
        }

        return "register.jsp";
    }
}
