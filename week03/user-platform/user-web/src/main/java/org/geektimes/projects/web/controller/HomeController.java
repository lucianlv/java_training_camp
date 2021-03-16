package org.geektimes.projects.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.geektimes.web.mvc.controller.PageController;

@Path("/a")
public class HomeController implements PageController {

    @Override
    @GET
    @Path("/b")
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return "index.jsp";
    }
}
