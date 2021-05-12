package pres.cocoadel.web.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PageController extends Controller {

    String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable;
}
