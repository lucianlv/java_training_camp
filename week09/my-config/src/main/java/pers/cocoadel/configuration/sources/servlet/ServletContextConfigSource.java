package pers.cocoadel.configuration.sources.servlet;


import pers.cocoadel.configuration.sources.MapBasedConfigSource;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

import static java.lang.String.format;

public class ServletContextConfigSource extends MapBasedConfigSource {

    private final ServletContext servletContext;

    public ServletContextConfigSource(ServletContext servletContext) {
        super(format("ServletContext[path:%s] Init Parameters", servletContext.getContextPath()), 500,false);
        this.servletContext = servletContext;
        initSource();
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        Enumeration<String> parameterNames = servletContext.getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            configData.put(parameterName, servletContext.getInitParameter(parameterName));
        }
    }
}
