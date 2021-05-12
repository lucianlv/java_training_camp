package pers.cocoadel.configuration.sources.servlet;

import pers.cocoadel.configuration.sources.MapBasedConfigSource;

import javax.servlet.ServletConfig;
import java.util.Enumeration;
import java.util.Map;

import static java.lang.String.format;

public class ServletConfigSource extends MapBasedConfigSource {

    private final ServletConfig servletConfig;

    public ServletConfigSource(ServletConfig servletConfig) {
        super(format("Servlet[name:%s] Init Parameters", servletConfig.getServletName()), 600);
        this.servletConfig = servletConfig;
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        Enumeration<String> parameterNames = servletConfig.getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            configData.put(parameterName, servletConfig.getInitParameter(parameterName));
        }
    }
}
