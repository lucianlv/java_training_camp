package org.geektimes.context;

import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;

public class ComponentContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) {
        ComponentContext context = new ClassicComponentContext();
        context.init(ctx);
    }
}
