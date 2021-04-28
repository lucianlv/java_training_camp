package org.geektimes.configuration.microprofile.config.source.servlet;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

/**
 * @Auther: liuyj
 * @Date: 2021/03/22/15:28
 * @Description:
 */
public class ServletContextConfigSource extends MapBasedConfigSource {

    private  Map<String,String> configData;

    protected ServletContextConfigSource(ServletContext servletContext) {
        super("ServletContext Init Parameters", 500);
        prepareConfigData(servletContext);
    }

    private void prepareConfigData(ServletContext servletContext) {
        Enumeration<String> initParameterNames = servletContext.getInitParameterNames();
        while(initParameterNames.hasMoreElements()){
            String nextElement = initParameterNames.nextElement();
            configData.put(nextElement,servletContext.getInitParameter(nextElement));
        }
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        this.configData=configData;
    }
}
