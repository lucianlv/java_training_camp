package org.geektimes.context.servlet;

import org.geektimes.context.ComponentContext;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * @Auther: liuyj
 * @Date: 2021/03/23/9:57
 * @Description:
 */
public class ServletComponentContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        ComponentContext context = new ComponentContext();
        context.init(ctx);
    }
}
