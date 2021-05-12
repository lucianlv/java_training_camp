package pers.cocoadel.user.platform.web.controller;

import pers.cocoadel.configuration.sources.servlet.ConfigRequestContext;
import pres.cocoadel.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * 输出 “Hello,World” Controller
 */
@Path("/hello")
public class HelloWorldController implements PageController {

    @GET
    @POST
    @Path("/world") // /hello/world -> HelloWorldController
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String applicationName = ConfigRequestContext.INSTANCE.get().getValue("application.name",String.class);
        request.setAttribute("applicationName",applicationName);
        request.getServletContext().log("application name: " + applicationName);
        return "hello-world.jsp";
    }
}