package pers.cocoadel.user.platform.web.controller;

import pers.cocoadel.user.platform.domain.User;
import pers.cocoadel.user.platform.exception.BusinessException;
import pers.cocoadel.user.platform.service.UserService;
import pres.cocoadel.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.*;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 注册逻辑的处理
 */
@Path("")
public class RegisterController implements PageController {

    @Resource(name = "bean/UserService")
    private UserService userService;

    @Resource(name = "bean/Validator")
    private Validator validator;

    private final AtomicLong idCreator = new AtomicLong();

    @Path("/register")
    @POST
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String phoneNumber = request.getParameter("phoneNumber");

            User user = new User();
            user.setId(idCreator.incrementAndGet());
            user.setName(email);
            user.setEmail(email);
            user.setPassword(password);
            user.setPhoneNumber(phoneNumber);

            // 校验结果
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            ConstraintViolation<User> violation = violations.stream().findFirst().orElse(null);
            if (violation != null) {
                throw new BusinessException(violation.getMessage());
            }

            System.out.println(user.toString());
            userService.signUp(user);
            System.out.println("注册成功！");
            return "register-success.jsp";
        } catch (Throwable e) {
            if(e instanceof BusinessException){
                request.setAttribute("error",e.getMessage());
            }else {
                request.setAttribute("error","服务器错误！");
            }
            e.printStackTrace();
            return "index.jsp";
        }
    }
}
