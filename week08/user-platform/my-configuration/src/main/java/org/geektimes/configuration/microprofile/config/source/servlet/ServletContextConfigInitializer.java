package org.geektimes.configuration.microprofile.config.source.servlet;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.configuration.microprofile.config.DefaultConfigProviderResolver;
import org.geektimes.configuration.microprofile.config.source.DefaultConfigContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

public class ServletContextConfigInitializer implements ServletContextListener {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    public static final String DEFAULT_CONFIG="defaultConfig";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        ServletContextConfigSource servletContextConfigSource = new ServletContextConfigSource(servletContext);
        // 获取当前 ClassLoader
        /**
         * 这句报错：
         *  java.lang.UnsupportedOperationException: Section 4.4 of the Servlet 3.0 specification does not permit this method to be called from a ServletContextListener that was not defined in web.xml, a web-fragment.xml file nor annotated with @WebListener
         */
//        ClassLoader classLoader = servletContext.getClassLoader();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        ConfigBuilder configBuilder = configProviderResolver.getBuilder();
        // 配置 ClassLoader
        configBuilder.forClassLoader(classLoader);
        // 默认配置源（内建的，静态的）
        configBuilder.addDefaultSources();
        // 通过发现配置源（动态的）
        configBuilder.addDiscoveredConverters();
        // 增加扩展配置源（基于 Servlet 引擎）
        configBuilder.withSources(servletContextConfigSource);
        // 获取 Config
        Config config = configBuilder.build();

        // 注册 Config 关联到当前 ClassLoader
        configProviderResolver.registerConfig(config, classLoader);

        String message = config.getValue("message", String.class);
        String value = config.getValue("application.name", String.class);
        logger.info("message："+message);
        logger.info("application.name："+value);
        logger.info("所有的属性："+config.getPropertyNames());

        servletContext.setAttribute(DEFAULT_CONFIG,configProviderResolver.getConfig(classLoader));

        DefaultConfigContext.set(config);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
