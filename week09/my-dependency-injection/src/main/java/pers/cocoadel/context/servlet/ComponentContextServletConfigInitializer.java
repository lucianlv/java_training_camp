package pers.cocoadel.context.servlet;

import pers.cocoadel.context.ComponentContext;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class ComponentContextServletConfigInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        ComponentContext context = new ComponentContext();
        context.init(ctx);
    }
}
